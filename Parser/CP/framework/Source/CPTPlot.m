#import "CPTExceptions.h"
#import "CPTFill.h"
#import "CPTGraph.h"
#import "CPTLegend.h"
#import "CPTLineStyle.h"
#import "CPTMutableNumericData.h"
#import "CPTMutableNumericData+TypeConversion.h"
#import "CPTNumericData.h"
#import "CPTNumericData+TypeConversion.h"
#import "CPTPathExtensions.h"
#import "CPTPlot.h"
#import "CPTPlotArea.h"
#import "CPTPlotAreaFrame.h"
#import "CPTPlotRange.h"
#import "CPTPlotSpace.h"
#import "CPTPlotSpaceAnnotation.h"
#import "CPTTextLayer.h"
#import "NSNumberExtensions.h"
#import "CPTUtilities.h"
#import <tgmath.h>

/**	@cond */
@interface CPTPlot()

@property (nonatomic, readwrite, assign) BOOL dataNeedsReloading;
@property (nonatomic, readwrite, retain) NSMutableDictionary *cachedData;

@property (nonatomic, readwrite, assign) BOOL needsRelabel;
@property (nonatomic, readwrite, assign) BOOL labelFormatterChanged;
@property (nonatomic, readwrite, assign) NSRange labelIndexRange;
@property (nonatomic, readwrite, retain) NSMutableArray *labelAnnotations;

@property (nonatomic, readwrite, assign) NSUInteger cachedDataCount;

-(CPTMutableNumericData *)numericDataForNumbers:(id)numbers;
-(void)setCachedDataType:(CPTNumericDataType)newDataType;
-(void)updateContentAnchorForLabel:(CPTPlotSpaceAnnotation *)label;

@end
/**	@endcond */

#pragma mark -

/**	@brief An abstract plot class.
 *
 *	Each data series on the graph is represented by a plot.
 **/
@implementation CPTPlot

/**	@property dataSource
 *	@brief The data source for the plot.
 **/
@synthesize dataSource;

/**	@property identifier
 *	@brief An object used to identify the plot in collections.
 **/
@synthesize identifier;

/**	@property title
 *	@brief The title of the plot displayed in the legend.
 **/
@synthesize title;

/**	@property plotSpace
 *	@brief The plot space for the plot.
 **/
@synthesize plotSpace;

/**	@property plotArea
 *	@brief The plot area for the plot.
 **/
@dynamic plotArea;

/**	@property dataNeedsReloading
 *	@brief If YES, the plot data will be reloaded from the data source before the layer content is drawn.
 **/
@synthesize dataNeedsReloading;

@synthesize cachedData;

/**	@property cachedDataCount
 *	@brief The number of data points stored in the cache.
 **/
@synthesize cachedDataCount;

/**	@property doublePrecisionCache
 *	@brief If YES, the cache holds data of type 'double', otherwise it holds NSNumber.
 **/
@dynamic doublePrecisionCache;

/**	@property cachePrecision
 *	@brief The numeric precision used to cache the plot data and perform all plot calculations. Defaults to CPTPlotCachePrecisionAuto.
 **/
@synthesize cachePrecision;

/**	@property doubleDataType
 *	@brief The CPTNumericDataType used to cache plot data as <code>double</code>.
 **/
@dynamic doubleDataType;

/**	@property decimalDataType
 *	@brief The CPTNumericDataType used to cache plot data as NSDecimal.
 **/
@dynamic decimalDataType;

/**	@property needsRelabel
 *	@brief If YES, the plot needs to be relabeled before the layer content is drawn.
 **/
@synthesize needsRelabel;

/**	@property labelOffset
 *	@brief The distance that labels should be offset from their anchor points. The direction of the offset is defined by subclasses.
 **/
@synthesize labelOffset;

/**	@property labelRotation
 *	@brief The rotation of the data labels in radians.
 *  Set this property to <code>M_PI/2.0</code> to have labels read up the screen, for example.
 **/
@synthesize labelRotation;

/**	@property labelField
 *	@brief The plot field identifier of the data field used to generate automatic labels.
 **/
@synthesize labelField;

/**	@property labelTextStyle
 *	@brief The text style used to draw the data labels.
 *	Set this property to <code>nil</code> to hide the data labels.
 **/
@synthesize labelTextStyle;

/**	@property labelFormatter
 *	@brief The number formatter used to format the data labels.
 *	Set this property to <code>nil</code> to hide the data labels.
 *  If you need a non-numerical label, such as a date, you can use a formatter than turns
 *  the numerical plot coordinate into a string (e.g., "Jan 10, 2010"). 
 *  The CPTTimeFormatter is useful for this purpose.
 **/
@synthesize labelFormatter;

@synthesize labelFormatterChanged;

@synthesize labelIndexRange;

@synthesize labelAnnotations;

#pragma mark -
#pragma mark init/dealloc

-(id)initWithFrame:(CGRect)newFrame
{
	if ( (self = [super initWithFrame:newFrame]) ) {
		cachedData = [[NSMutableDictionary alloc] initWithCapacity:5];
		cachedDataCount = 0;
		cachePrecision = CPTPlotCachePrecisionAuto;
		dataSource = nil;
		identifier = nil;
		title = nil;
		plotSpace = nil;
        dataNeedsReloading = NO;
		needsRelabel = YES;
		labelOffset = 0.0;
		labelRotation = 0.0;
		labelField = 0;
		labelTextStyle = nil;
		labelFormatter = nil;
		labelFormatterChanged = YES;
		labelIndexRange = NSMakeRange(0, 0);
		labelAnnotations = nil;
		
		self.masksToBounds = YES;
		self.needsDisplayOnBoundsChange = YES;
	}
	return self;
}

-(id)initWithLayer:(id)layer
{
	if ( (self = [super initWithLayer:layer]) ) {
		CPTPlot *theLayer = (CPTPlot *)layer;
		
		cachedData = [theLayer->cachedData retain];
		cachedDataCount = theLayer->cachedDataCount;
		cachePrecision = theLayer->cachePrecision;
		dataSource = theLayer->dataSource;
		identifier = [theLayer->identifier retain];
		title = [theLayer->title retain];
		plotSpace = [theLayer->plotSpace retain];
		dataNeedsReloading = theLayer->dataNeedsReloading;
		needsRelabel = theLayer->needsRelabel;
		labelOffset = theLayer->labelOffset;
		labelRotation = theLayer->labelRotation;
		labelField = theLayer->labelField;
		labelTextStyle = [theLayer->labelTextStyle retain];
		labelFormatter = [theLayer->labelFormatter retain];
		labelFormatterChanged = theLayer->labelFormatterChanged;
		labelIndexRange = theLayer->labelIndexRange;
		labelAnnotations = [theLayer->labelAnnotations retain];
	}
	return self;
}

-(void)dealloc
{
	[cachedData release];
    [identifier release];
	[title release];
    [plotSpace release];
	[labelTextStyle release];
	[labelFormatter release];
	[labelAnnotations release];
	
    [super dealloc];
}

#pragma mark -
#pragma mark Bindings

#if TARGET_IPHONE_SIMULATOR || TARGET_OS_IPHONE
#else

-(Class)valueClassForBinding:(NSString *)binding
{
	return [NSArray class];
}

#endif

#pragma mark -
#pragma mark Drawing

-(void)drawInContext:(CGContextRef)theContext
{
    [self reloadDataIfNeeded];
    [super drawInContext:theContext];
}

#pragma mark -
#pragma mark Layout

+(CGFloat)defaultZPosition 
{
	return CPTDefaultZPositionPlot;
}

-(void)layoutSublayers 
{
	[self relabel];
    [super layoutSublayers];
}

#pragma mark -
#pragma mark Data Source

/**	@brief Marks the receiver as needing the data source reloaded before the content is next drawn.
 **/
-(void)setDataNeedsReloading
{
	self.dataNeedsReloading = YES;
}

/**	@brief Reload all plot data from the data source immediately.
 **/
-(void)reloadData
{
	[self.cachedData removeAllObjects];
	self.cachedDataCount = 0;
	[self reloadDataInIndexRange:NSMakeRange(0, [self.dataSource numberOfRecordsForPlot:self])];
}

/**	@brief Reload plot data from the data source only if the data cache is out of date.
 **/
-(void)reloadDataIfNeeded
{
	if ( self.dataNeedsReloading ) {
		[self reloadData];
	}
}

/**	@brief Reload plot data in the given index range from the data source immediately.
 *	@param indexRange The index range to load.
 **/
-(void)reloadDataInIndexRange:(NSRange)indexRange
{
	NSParameterAssert(NSMaxRange(indexRange) <= [self.dataSource numberOfRecordsForPlot:self]);
	
    self.dataNeedsReloading = NO;
	[self relabelIndexRange:indexRange];
}

/**	@brief Insert records into the plot data cache at the given index.
 *	@param index The starting index of the new records.
 *	@param numberOfRecords The number of records to insert.
 **/
-(void)insertDataAtIndex:(NSUInteger)index numberOfRecords:(NSUInteger)numberOfRecords
{
	NSParameterAssert(index <= self.cachedDataCount);
	
	for ( CPTMutableNumericData *numericData in [self.cachedData allValues] ) {
		size_t sampleSize = numericData.sampleBytes;
		size_t length = sampleSize * numberOfRecords;
		
		[(NSMutableData *)numericData.data increaseLengthBy:length];
		
		void *start = [numericData samplePointer:index];
		size_t bytesToMove = numericData.data.length - (index + numberOfRecords) * sampleSize;
		if ( bytesToMove > 0 ) {
			memmove(start + length, start, bytesToMove);
		}
	}
	
	self.cachedDataCount += numberOfRecords;
	[self reloadDataInIndexRange:NSMakeRange(index, self.cachedDataCount - index)];
}

/**	@brief Delete records in the given index range from the plot data cache.
 *	@param indexRange The index range of the data records to remove.
 **/
-(void)deleteDataInIndexRange:(NSRange)indexRange
{
	NSParameterAssert(NSMaxRange(indexRange) <= self.cachedDataCount);
	
	for ( CPTMutableNumericData *numericData in [self.cachedData allValues] ) {
		size_t sampleSize = numericData.sampleBytes;
		void *start = [numericData samplePointer:indexRange.location];
		size_t length = sampleSize * indexRange.length;
		size_t bytesToMove = numericData.data.length - (indexRange.location + indexRange.length) * sampleSize;
		if ( bytesToMove > 0 ) {
			memmove(start, start + length, bytesToMove);
		}
		
		NSMutableData *dataBuffer = (NSMutableData *)numericData.data;
		dataBuffer.length -= length;
	}
	
	self.cachedDataCount -= indexRange.length;
	[self relabelIndexRange:NSMakeRange(indexRange.location, self.cachedDataCount - indexRange.location)];
	[self setNeedsDisplay];
}

/**	@brief Gets a range of plot data for the given plot and field.
 *	@param fieldEnum The field index.
 *	@param indexRange The range of the data indexes of interest.
 *	@return An array of data points.
 **/
-(id)numbersFromDataSourceForField:(NSUInteger)fieldEnum recordIndexRange:(NSRange)indexRange 
{
    id numbers;  // can be CPTNumericData, NSArray, or NSData
    id <CPTPlotDataSource> theDataSource = self.dataSource;
	
    if ( theDataSource ) {
		if ( [theDataSource respondsToSelector:@selector(dataForPlot:field:recordIndexRange:)] ) {
			numbers = [theDataSource dataForPlot:self field:fieldEnum recordIndexRange:indexRange];
		}
        else if ( [theDataSource respondsToSelector:@selector(doublesForPlot:field:recordIndexRange:)] ) {
            numbers = [NSMutableData dataWithLength:sizeof(double)*indexRange.length];
            double *fieldValues = [numbers mutableBytes];
            double *doubleValues = [theDataSource doublesForPlot:self field:fieldEnum recordIndexRange:indexRange];
            memcpy( fieldValues, doubleValues, sizeof(double)*indexRange.length );
        }
        else if ( [theDataSource respondsToSelector:@selector(numbersForPlot:field:recordIndexRange:)] ) {
            numbers = [NSArray arrayWithArray:[theDataSource numbersForPlot:self field:fieldEnum recordIndexRange:indexRange]];
        }
        else if ( [theDataSource respondsToSelector:@selector(doubleForPlot:field:recordIndex:)] ) {
            NSUInteger recordIndex;
            NSMutableData *fieldData = [NSMutableData dataWithLength:sizeof(double)*indexRange.length];
            double *fieldValues = [fieldData mutableBytes];
            for ( recordIndex = indexRange.location; recordIndex < indexRange.location + indexRange.length; ++recordIndex ) {
                double number = [theDataSource doubleForPlot:self field:fieldEnum recordIndex:recordIndex];
                *fieldValues++ = number;
            }
            numbers = fieldData;
        }
        else {
            BOOL respondsToSingleValueSelector = [theDataSource respondsToSelector:@selector(numberForPlot:field:recordIndex:)];
            NSUInteger recordIndex;
            NSMutableArray *fieldValues = [NSMutableArray arrayWithCapacity:indexRange.length];
            for ( recordIndex = indexRange.location; recordIndex < indexRange.location + indexRange.length; recordIndex++ ) {
                if ( respondsToSingleValueSelector ) {
                    NSNumber *number = [theDataSource numberForPlot:self field:fieldEnum recordIndex:recordIndex];
					if ( number ) {
						[fieldValues addObject:number];
					}
					else {
						[fieldValues addObject:[NSNull null]];
					}
					
                }
                else {
                    [fieldValues addObject:[NSDecimalNumber zero]];
                }
            }
            numbers = fieldValues;
        }
    }
    else {
        numbers = [NSArray array];
    }
    
    return numbers;
}

#pragma mark -
#pragma mark Data Caching

/**	@brief Copies an array of numbers to the cache.
 *	@param numbers An array of numbers to cache. Can be a CPTNumericData, NSArray, or NSData (NSData is assumed to be a c-style array of type <code>double</code>).
 *	@param fieldEnum The field enumerator identifying the field.
 **/
-(void)cacheNumbers:(id)numbers forField:(NSUInteger)fieldEnum 
{
	NSNumber *cacheKey = [NSNumber numberWithUnsignedInteger:fieldEnum];
	
	if ( numbers ) {
		CPTMutableNumericData *mutableNumbers = [self numericDataForNumbers:numbers];
		
		NSUInteger sampleCount = mutableNumbers.numberOfSamples;
		if ( sampleCount > 0 ) {
			[self.cachedData setObject:mutableNumbers forKey:cacheKey];
		}
		else {
			[self.cachedData removeObjectForKey:cacheKey];
		}
		
		self.cachedDataCount = sampleCount;
		
		switch ( self.cachePrecision ) {
			case CPTPlotCachePrecisionAuto:
				[self setCachedDataType:mutableNumbers.dataType];
				break;
			case CPTPlotCachePrecisionDouble:
				[self setCachedDataType:self.doubleDataType];
				break;
			case CPTPlotCachePrecisionDecimal:
				[self setCachedDataType:self.decimalDataType];
				break;
		}
	}
	else {
		[self.cachedData removeObjectForKey:cacheKey];
		self.cachedDataCount = 0;
	}
	self.needsRelabel = YES;
	[self setNeedsDisplay];
}

/**	@brief Copies an array of numbers to replace a part of the cache.
 *	@param numbers An array of numbers to cache. Can be a CPTNumericData, NSArray, or NSData (NSData is assumed to be a c-style array of type <code>double</code>).
 *	@param fieldEnum The field enumerator identifying the field.
 *	@param index The index of the first data point to replace.
 **/
-(void)cacheNumbers:(id)numbers forField:(NSUInteger)fieldEnum atRecordIndex:(NSUInteger)index
{
	if ( numbers ) {
		CPTMutableNumericData *mutableNumbers = [self numericDataForNumbers:numbers];
		
		NSUInteger sampleCount = mutableNumbers.numberOfSamples;
		if ( sampleCount > 0 ) {
			// Ensure the new data is the same type as the cache
			switch ( self.cachePrecision ) {
				case CPTPlotCachePrecisionAuto:
					[self setCachedDataType:mutableNumbers.dataType];
					break;
				case CPTPlotCachePrecisionDouble: {
					CPTNumericDataType newType = self.doubleDataType;
					[self setCachedDataType:newType];
					mutableNumbers.dataType = newType;
				}
					break;
				case CPTPlotCachePrecisionDecimal: {
					CPTNumericDataType newType = self.decimalDataType;
					[self setCachedDataType:newType];
					mutableNumbers.dataType = newType;
				}
					break;
			}
			
			// Ensure the data cache exists and is the right size
			NSNumber *cacheKey = [NSNumber numberWithUnsignedInteger:fieldEnum];
			CPTMutableNumericData *cachedNumbers = [self.cachedData objectForKey:cacheKey];
			if ( !cachedNumbers ) {
				cachedNumbers = [CPTMutableNumericData numericDataWithData:[NSData data]
																 dataType:mutableNumbers.dataType
																	shape:nil];
				[self.cachedData setObject:cachedNumbers forKey:cacheKey];
			}
			NSUInteger numberOfRecords = [self.dataSource numberOfRecordsForPlot:self];
			((NSMutableData *)cachedNumbers.data).length = numberOfRecords * cachedNumbers.sampleBytes;
			
			// Update the cache
			self.cachedDataCount = numberOfRecords;
			
			NSUInteger startByte = index * cachedNumbers.sampleBytes;
			void *cachePtr = cachedNumbers.mutableBytes + startByte;
			size_t numberOfBytes = MIN(mutableNumbers.data.length, cachedNumbers.data.length - startByte);
			memcpy(cachePtr, mutableNumbers.bytes, numberOfBytes);
			
			[self relabelIndexRange:NSMakeRange(index, sampleCount)];
		}
		[self setNeedsDisplay];
	}
}

-(CPTMutableNumericData *)numericDataForNumbers:(id)numbers
{
	CPTMutableNumericData *mutableNumbers = nil;
	CPTNumericDataType loadedDataType;
	
	if ( [numbers isKindOfClass:[CPTNumericData class]] ) {
		mutableNumbers = [numbers mutableCopy];
		// ensure the numeric data is in a supported format; default to double if not already NSDecimal
		if ( !CPTDataTypeEqualToDataType(mutableNumbers.dataType, self.decimalDataType) ) {
			mutableNumbers.dataType = self.doubleDataType;
		}
	}
	else if ( [numbers isKindOfClass:[NSData class]] ) {
		loadedDataType = self.doubleDataType;
		mutableNumbers = [[CPTMutableNumericData alloc] initWithData:numbers dataType:loadedDataType shape:nil];
	}
	else if ( [numbers isKindOfClass:[NSArray class]] ) {
		if ( ((NSArray *)numbers).count == 0 ) {
			loadedDataType = self.doubleDataType;
		}
		else if ( [[(NSArray *)numbers objectAtIndex:0] isKindOfClass:[NSDecimalNumber class]] ) {
			loadedDataType = self.decimalDataType;
		} else {
			loadedDataType = self.doubleDataType;
		}
		
		mutableNumbers = [[CPTMutableNumericData alloc] initWithArray:numbers dataType:loadedDataType shape:nil];
	}
	else {
		[NSException raise:CPTException format:@"Unsupported number array format"];
	}
	
	return [mutableNumbers autorelease];
}

-(BOOL)doublePrecisionCache
{
	BOOL result = NO;
	switch ( self.cachePrecision ) {
		case CPTPlotCachePrecisionAuto: {
			NSArray *cachedObjects = [self.cachedData allValues];
			if ( cachedObjects.count > 0 ) {
				result = CPTDataTypeEqualToDataType(((CPTMutableNumericData *)[cachedObjects objectAtIndex:0]).dataType, self.doubleDataType);
			}
		}
			break;
		case CPTPlotCachePrecisionDouble:
			result = YES;
			break;
		default:
			// not double precision
			break;
	}
	return result;
}

/**	@brief Retrieves an array of numbers from the cache.
 *	@param fieldEnum The field enumerator identifying the field.
 *	@return The array of cached numbers.
 **/
-(CPTMutableNumericData *)cachedNumbersForField:(NSUInteger)fieldEnum 
{
    return [self.cachedData objectForKey:[NSNumber numberWithUnsignedInteger:fieldEnum]];
}

/**	@brief Retrieves a single number from the cache.
 *	@param fieldEnum The field enumerator identifying the field.
 *	@param index The index of the desired data value.
 *	@return The cached number.
 **/
-(NSNumber *)cachedNumberForField:(NSUInteger)fieldEnum recordIndex:(NSUInteger)index
{
    CPTMutableNumericData *numbers = [self cachedNumbersForField:fieldEnum];
	return [numbers sampleValue:index];
}

/**	@brief Retrieves a single number from the cache.
 *	@param fieldEnum The field enumerator identifying the field.
 *	@param index The index of the desired data value.
 *	@return The cached number or NAN if no data is cached for the requested field.
 **/
-(double)cachedDoubleForField:(NSUInteger)fieldEnum recordIndex:(NSUInteger)index
{
    CPTMutableNumericData *numbers = [self cachedNumbersForField:fieldEnum];
	if ( numbers ) {
		switch ( numbers.dataTypeFormat ) {
			case CPTFloatingPointDataType: {
				double *doubleNumber = (double *)[numbers samplePointer:index];
				return *doubleNumber;
			}
				break;
			case CPTDecimalDataType: {
				NSDecimal *decimalNumber = (NSDecimal *)[numbers samplePointer:index];
				return CPTDecimalDoubleValue(*decimalNumber);
			}
				break;
			default:
				[NSException raise:CPTException format:@"Unsupported data type format"];
				break;
		}
	}
	return NAN;
}

/**	@brief Retrieves a single number from the cache.
 *	@param fieldEnum The field enumerator identifying the field.
 *	@param index The index of the desired data value.
 *	@return The cached number or NAN if no data is cached for the requested field.
 **/
-(NSDecimal)cachedDecimalForField:(NSUInteger)fieldEnum recordIndex:(NSUInteger)index
{
    CPTMutableNumericData *numbers = [self cachedNumbersForField:fieldEnum];
	if ( numbers ) {
		switch ( numbers.dataTypeFormat ) {
			case CPTFloatingPointDataType: {
				double *doubleNumber = (double *)[numbers samplePointer:index];
				return CPTDecimalFromDouble(*doubleNumber);
			}
				break;
			case CPTDecimalDataType: {
				NSDecimal *decimalNumber = (NSDecimal *)[numbers samplePointer:index];
				return *decimalNumber;
			}
				break;
			default:
				[NSException raise:CPTException format:@"Unsupported data type format"];
				break;
		}
	}
	return CPTDecimalNaN();
}

-(void)setCachedDataType:(CPTNumericDataType)newDataType
{
	for ( CPTMutableNumericData *numericData in [self.cachedData allValues] ) {
		numericData.dataType = newDataType;
	}
}

-(CPTNumericDataType)doubleDataType
{
	return CPTDataType(CPTFloatingPointDataType, sizeof(double), CFByteOrderGetCurrent());
}

-(CPTNumericDataType)decimalDataType
{
	return CPTDataType(CPTDecimalDataType, sizeof(NSDecimal), CFByteOrderGetCurrent());
}

#pragma mark -
#pragma mark Data Ranges

/**	@brief Determines the smallest plot range that fully encloses the data for a particular field.
 *	@param fieldEnum The field enumerator identifying the field.
 *	@return The plot range enclosing the data.
 **/
-(CPTPlotRange *)plotRangeForField:(NSUInteger)fieldEnum 
{
    if ( self.dataNeedsReloading ) [self reloadData];
    CPTMutableNumericData *numbers = [self cachedNumbersForField:fieldEnum];
    CPTPlotRange *range = nil;
    if ( numbers.numberOfSamples > 0 ) {
		NSMutableArray *numberArray = [[numbers sampleArray] mutableCopy];
        [numberArray removeObject:[NSNull null]];
        [numberArray removeObject:[NSDecimalNumber notANumber]];
        if ( numberArray.count > 0 ) {
            NSNumber *min = [numberArray valueForKeyPath:@"@min.self"];
            NSNumber *max = [numberArray valueForKeyPath:@"@max.self"];
            NSDecimal length = CPTDecimalSubtract([max decimalValue], [min decimalValue]);
            range = [CPTPlotRange plotRangeWithLocation:[min decimalValue] length:length];
        }
        [numberArray release];
    }
    return range;
}

/**	@brief Determines the smallest plot range that fully encloses the data for a particular coordinate.
 *	@param coord The coordinate identifier.
 *	@return The plot range enclosing the data.
 **/
-(CPTPlotRange *)plotRangeForCoordinate:(CPTCoordinate)coord 
{
    NSArray *fields = [self fieldIdentifiersForCoordinate:coord];
    if ( fields.count == 0 ) return nil;
    
    CPTPlotRange *unionRange = nil;
    for ( NSNumber *field in fields ) {
    	CPTPlotRange *currentRange = [self plotRangeForField:field.unsignedIntValue];
    	if ( !unionRange ) 
        	unionRange = currentRange;
        else
        	[unionRange unionPlotRange:[self plotRangeForField:field.unsignedIntValue]];
    }
    
    return unionRange;
}

#pragma mark -
#pragma mark Data Labels

/**	@brief Marks the receiver as needing to update the labels before the content is next drawn.
 **/
-(void)setNeedsRelabel
{
	self.labelIndexRange = NSMakeRange(0, self.cachedDataCount);
    self.needsRelabel = YES;
}

/**	@brief Updates the data labels.
 **/
-(void)relabel
{
    if ( !self.needsRelabel ) return;
	
    self.needsRelabel = NO;
	
	id <CPTPlotDataSource> theDataSource = self.dataSource;
	CPTMutableTextStyle *dataLabelTextStyle = self.labelTextStyle;
	NSNumberFormatter *dataLabelFormatter = self.labelFormatter;
	
	BOOL dataSourceProvidesLabels = [theDataSource respondsToSelector:@selector(dataLabelForPlot:recordIndex:)];
	BOOL plotProvidesLabels = dataLabelTextStyle && dataLabelFormatter;
	
	if ( !dataSourceProvidesLabels && !plotProvidesLabels ) {
		Class annotationClass = [CPTAnnotation class];
		for ( CPTAnnotation *annotation in self.labelAnnotations) {
			if ( [annotation isKindOfClass:annotationClass] ) {
				[self removeAnnotation:annotation];
			}
		}
		self.labelAnnotations = nil;
		return;
	}
	
	NSUInteger sampleCount = self.cachedDataCount;
	NSRange indexRange = self.labelIndexRange;
	NSUInteger maxIndex = NSMaxRange(indexRange);
	
	if ( !self.labelAnnotations ) {
		self.labelAnnotations = [NSMutableArray arrayWithCapacity:sampleCount];
	}
	
	CPTPlotSpace *thePlotSpace = self.plotSpace;
	CGFloat theRotation = self.labelRotation;
	NSMutableArray *labelArray = self.labelAnnotations;
	NSUInteger oldLabelCount = labelArray.count;
	Class nullClass = [NSNull class];
	CPTMutableNumericData *labelFieldDataCache = [self cachedNumbersForField:self.labelField];
	
	for ( NSUInteger i = indexRange.location; i < maxIndex; i++ ) {
		CPTLayer *newLabelLayer = nil;
		
		NSNumber *dataValue = [labelFieldDataCache sampleValue:i];
		
		if ( isnan([dataValue doubleValue]) ) {
			newLabelLayer = nil;
		}
		else {
			if ( dataSourceProvidesLabels ) {
				newLabelLayer = [[theDataSource dataLabelForPlot:self recordIndex:i] retain];
			}
			
			if ( !newLabelLayer && plotProvidesLabels ) {
				NSString *labelString = [dataLabelFormatter stringForObjectValue:dataValue];
				newLabelLayer = [[CPTTextLayer alloc] initWithText:labelString style:dataLabelTextStyle];
			}
			
			if ( [newLabelLayer isKindOfClass:nullClass] ) {
				[newLabelLayer release];
				newLabelLayer = nil;
			}
		}
		
		CPTPlotSpaceAnnotation *labelAnnotation;
		if ( i < oldLabelCount ) {
			labelAnnotation = [labelArray objectAtIndex:i];
		}
		else {
			labelAnnotation = [[CPTPlotSpaceAnnotation alloc] initWithPlotSpace:thePlotSpace anchorPlotPoint:nil];
			[labelArray addObject:labelAnnotation];
			[self addAnnotation:labelAnnotation];
			[labelAnnotation release];
		}
		
		labelAnnotation.contentLayer = newLabelLayer;
		labelAnnotation.rotation = theRotation;
		[self positionLabelAnnotation:labelAnnotation forIndex:i];
		[self updateContentAnchorForLabel:labelAnnotation];
		
		[newLabelLayer release];
	}
	
	// remove labels that are no longer needed
	Class annotationClass = [CPTAnnotation class];
	while ( labelArray.count > sampleCount ) {
		CPTAnnotation *oldAnnotation = [labelArray objectAtIndex:labelArray.count - 1];
		if ( [oldAnnotation isKindOfClass:annotationClass] ) {
			[self removeAnnotation:oldAnnotation];
		}
		[labelArray removeLastObject];
	}
}	

/**	@brief Sets the labelIndexRange and informs the receiver that it needs to relabel.
 *	@param indexRange The new indexRange for the labels.
 *
 *	@todo Needs more documentation.
 **/
-(void)relabelIndexRange:(NSRange)indexRange
{
	self.labelIndexRange = indexRange;
	self.needsRelabel = YES;
}

-(void)updateContentAnchorForLabel:(CPTPlotSpaceAnnotation *)label
{
	CGPoint displacement = label.displacement;
	if ( CGPointEqualToPoint(displacement, CGPointZero) ) {
		displacement.y = 1.0; // put the label above the data point if zero displacement
	}
	CGFloat angle = (CGFloat)M_PI + atan2(displacement.y, displacement.x) - label.rotation;
	CGFloat newAnchorX = cos(angle);
	CGFloat newAnchorY = sin(angle);
	
	if ( ABS(newAnchorX) <= ABS(newAnchorY) ) {
		newAnchorX /= ABS(newAnchorY);
		newAnchorY = signbit(newAnchorY) ? -1.0 : 1.0;
	}
	else {
		newAnchorY /= ABS(newAnchorX);
		newAnchorX = signbit(newAnchorX) ? -1.0 : 1.0;
	}
	
	label.contentAnchorPoint = CGPointMake((newAnchorX + 1.0) / 2.0, (newAnchorY + 1.0) / 2.0);
}

#pragma mark -
#pragma mark Legends

/**	@brief The number of legend entries provided by this plot.
 *	@return The number of legend entries.
 **/
-(NSUInteger)numberOfLegendEntries
{
	return 1;
}

/**	@brief The title text of a legend entry.
 *	@param index The index of the desired title.
 *	@return The title of the legend entry at the requested index.
 **/
-(NSString *)titleForLegendEntryAtIndex:(NSUInteger)index
{
	NSString *legendTitle = self.title;
	
	if ( !legendTitle ) {
		if ( [self.identifier isKindOfClass:[NSString class]] ) {
			legendTitle = (NSString *)self.identifier;
		}
	}
	
	return legendTitle;
}

/**	@brief Draws the legend swatch of a legend entry.
 *	Subclasses should call super to draw the background fill and border.
 *	@param legend The legend being drawn.
 *	@param index The index of the desired swatch.
 *	@param rect The bounding rectangle where the swatch should be drawn.
 *	@param context The graphics context to draw into.
 **/
-(void)drawSwatchForLegend:(CPTLegend *)legend atIndex:(NSUInteger)index inRect:(CGRect)rect inContext:(CGContextRef)context
{
	CPTFill *theFill = legend.swatchFill;
	CPTLineStyle *theLineStyle = legend.swatchBorderLineStyle;
	
	if ( theFill || theLineStyle ) {
		CGPathRef swatchPath;
		CGFloat radius = legend.swatchCornerRadius;
		if ( radius > 0.0 ) {
			radius = MIN(MIN(radius, rect.size.width / 2.0), rect.size.height / 2.0);
			swatchPath = CreateRoundedRectPath(rect, radius);
		}
		else {
			CGMutablePathRef mutablePath = CGPathCreateMutable();
			CGPathAddRect(mutablePath, NULL, rect);
			swatchPath = mutablePath;
		}

		if ( theFill ) {
			CGContextBeginPath(context);
			CGContextAddPath(context, swatchPath);
			[theFill fillPathInContext:context];
		}
		
		if ( theLineStyle ) {
			[theLineStyle setLineStyleInContext:context];
			CGContextBeginPath(context);
			CGContextAddPath(context, swatchPath);
			CGContextStrokePath(context);
		}
		
		CGPathRelease(swatchPath);
	}
}

#pragma mark -
#pragma mark Accessors

-(void)setTitle:(NSString *)newTitle
{
    if ( newTitle != title ) {
        [title release];
        title = [newTitle copy];
		[[NSNotificationCenter defaultCenter] postNotificationName:CPTLegendNeedsRedrawForPlotNotification object:self];
    }
}

-(void)setDataSource:(id <CPTPlotDataSource>)newSource 
{
    if ( newSource != dataSource ) {
        dataSource = newSource;
        [self setDataNeedsReloading];
    }
}

-(void)setDataNeedsReloading:(BOOL)newDataNeedsReloading
{
    if ( newDataNeedsReloading != dataNeedsReloading ) {
        dataNeedsReloading = newDataNeedsReloading;
        if ( dataNeedsReloading ) {
			[self setNeedsDisplay];
        }
    }
}

-(CPTPlotArea *)plotArea
{
	return self.graph.plotAreaFrame.plotArea;
}

-(void)setNeedsRelabel:(BOOL)newNeedsRelabel 
{
    if ( newNeedsRelabel != needsRelabel ) {
        needsRelabel = newNeedsRelabel;
        if ( needsRelabel ) {
            [self setNeedsLayout];
        }
    }
}

-(void)setLabelTextStyle:(CPTMutableTextStyle *)newStyle 
{
	if ( newStyle != labelTextStyle ) {
		[labelTextStyle release];
		labelTextStyle = [newStyle copy];
		
		if ( labelTextStyle && !self.labelFormatter ) {
			NSNumberFormatter *newFormatter = [[NSNumberFormatter alloc] init];
			newFormatter.minimumIntegerDigits = 1;
			newFormatter.maximumFractionDigits = 1; 
			newFormatter.minimumFractionDigits = 1;
			self.labelFormatter = newFormatter;
			[newFormatter release];
		}
		
		self.needsRelabel = YES;
	}
}

-(void)setLabelOffset:(CGFloat)newOffset 
{
    if ( newOffset != labelOffset ) {
        labelOffset = newOffset;
		self.needsRelabel = YES;
    }
}

-(void)setLabelRotation:(CGFloat)newRotation 
{
    if ( newRotation != labelRotation ) {
        labelRotation = newRotation;
		self.needsRelabel = YES;
    }
}

-(void)setLabelFormatter:(NSNumberFormatter *)newTickLabelFormatter 
{
    if ( newTickLabelFormatter != labelFormatter ) {
        [labelFormatter release];
        labelFormatter = [newTickLabelFormatter retain];
		self.labelFormatterChanged = YES;
        self.needsRelabel = YES;
    }
}

-(void)setCachePrecision:(CPTPlotCachePrecision)newPrecision
{
	if ( newPrecision != cachePrecision ) {
		cachePrecision = newPrecision;
		switch ( cachePrecision ) {
			case CPTPlotCachePrecisionAuto:
				// don't change data already in the cache
				break;
			case CPTPlotCachePrecisionDouble:
				[self setCachedDataType:self.doubleDataType];
				break;
			case CPTPlotCachePrecisionDecimal:
				[self setCachedDataType:self.decimalDataType];
				break;
			default:
				[NSException raise:NSInvalidArgumentException format:@"Invalid cache precision"];
				break;
		}
	}
}

@end

#pragma mark -

@implementation CPTPlot(AbstractMethods)

#pragma mark -
#pragma mark Fields

/**	@brief Number of fields in a plot data record.
 *	@return The number of fields.
 **/
-(NSUInteger)numberOfFields 
{
    return 0;
}

/**	@brief Identifiers (enum values) identifying the fields.
 *	@return Array of NSNumbers for the various field identifiers.
 **/
-(NSArray *)fieldIdentifiers 
{
    return [NSArray array];
}

/**	@brief The field identifiers that correspond to a particular coordinate.
 *  @param coord The coordinate for which the corresponding field identifiers are desired.
 *	@return Array of NSNumbers for the field identifiers.
 **/
-(NSArray *)fieldIdentifiersForCoordinate:(CPTCoordinate)coord 
{
    return [NSArray array];
}

#pragma mark -
#pragma mark Data Labels

/**	@brief Adjusts the position of the data label annotation for the plot point at the given index.
 *  @param label The annotation for the data label.
 *  @param index The data index for the label.
 **/
-(void)positionLabelAnnotation:(CPTPlotSpaceAnnotation *)label forIndex:(NSUInteger)index
{
	// do nothing--implementation provided by subclasses
}

@end

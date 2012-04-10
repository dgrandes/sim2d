//
//  asdAppDelegate.m
//  asd
//
//  Created by Matias Santiago Pan on 8/3/11.
//  Copyright 2011 Nasa Trained Monkeys. All rights reserved.
//

#import "asdAppDelegate.h"

#import "asdViewController.h"

@implementation asdAppDelegate


@synthesize window=_window;

@synthesize viewController=_viewController;

+ (NSString *)pathInDocumentsDirectory:(NSString *)filename {
    // Get list of document directories in sandbox
    NSArray *documentDirectories = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    
    // Get one and only one document directory from that list
    NSString *documentDirectory = [documentDirectories objectAtIndex:0];
    
    // Append passed in file name to that directory, return it
    return [documentDirectory stringByAppendingPathComponent:filename];
    
}

- (void)printFormattedQMatrix {
    NSMutableDictionary *groupsDictionary = [NSMutableDictionary dictionary];
    
    NSString *pDatabasePath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:@"interpreter.txt"];
    NSData *matrixData = [NSData dataWithContentsOfFile:pDatabasePath];
    NSString *matrixString = [[NSString alloc] initWithData:matrixData encoding:NSUTF8StringEncoding];
    
    NSRegularExpression *regex = [NSRegularExpression 
                                  regularExpressionWithPattern:
                                  @"(\\[.*?\\])\\s>\\s([^\\s]+)\\s==\\s([-\\d\\w\\.]+)"
                                  options:NSRegularExpressionCaseInsensitive|NSRegularExpressionDotMatchesLineSeparators error:nil];
    
    NSArray *matches = [regex matchesInString:matrixString options:0 range:NSMakeRange(0, [matrixString length])];
    
    for (NSTextCheckingResult *match in matches) {
        NSRange groupMatchRange = [match rangeAtIndex:1];
        NSRange actionMatchRange = [match rangeAtIndex:2];
        NSRange qValueMatchRange = [match rangeAtIndex:3];
        
        NSString *groupString = [matrixString substringWithRange:groupMatchRange];
        NSString *actionString = [matrixString substringWithRange:actionMatchRange];
        NSString *qValueString = [matrixString substringWithRange:qValueMatchRange];
        
        NSMutableDictionary *actionsDictionary = [groupsDictionary objectForKey:groupString];
        
        if (!actionsDictionary) {
            actionsDictionary = [NSMutableDictionary dictionary];
            [groupsDictionary setValue:actionsDictionary forKey:groupString];
        }
        
        [actionsDictionary setValue:qValueString forKey:actionString];
    }
    
    
    NSMutableString *outputString = [NSMutableString string];
    [outputString appendFormat:@"\nGRUPO                             %@  %@  %@  %@", 
     @"ACTION_LEFT", @"ACTION_RIGHT", @"ACTION_NONE", @"ACTION_BACK"];
    
    for (NSString *groupString in [groupsDictionary allKeys]) {
        NSDictionary *actionsDictionary = [groupsDictionary valueForKey:groupString];
        
        [outputString appendFormat:@"\n%@ %+f    %+f     %+f    %+f",
         groupString,
         [[actionsDictionary objectForKey:@"ACTION_LEFT"] floatValue],
         [[actionsDictionary objectForKey:@"ACTION_RIGHT"] floatValue],
         [[actionsDictionary objectForKey:@"ACTION_NONE"] floatValue],
         [[actionsDictionary objectForKey:@"ACTION_BACK"] floatValue]];
        
    }
    
    [[outputString dataUsingEncoding:NSUTF8StringEncoding] writeToFile:[asdAppDelegate pathInDocumentsDirectory:@"Matrix.txt"] atomically:NO];
}

- (void)printRunExtract {
    
}

- (void)graphSuccessVsCollisions {
    NSString *pDatabasePath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:@"interpreter.txt"];
    NSData *matrixData = [NSData dataWithContentsOfFile:pDatabasePath];
    NSString *matrixString = [[NSString alloc] initWithData:matrixData encoding:NSUTF8StringEncoding];
    
    //NSLog(@"%@", matrixString);
    
    NSRegularExpression *regex = [NSRegularExpression 
                                  regularExpressionWithPattern:
                                  @"[-\\d]+\\s[-\\d\\.]+\\s[\\d]+\\s([\\d]+)\\s([\\d]+)"
                                  options:NSRegularExpressionCaseInsensitive|NSRegularExpressionDotMatchesLineSeparators error:nil];
    
    NSArray *matches = [regex matchesInString:matrixString options:0 range:NSMakeRange(0, [matrixString length])];
    
    NSMutableString *outputString = [NSMutableString string];
    [outputString appendFormat:@"<html><head><script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script><script type=\"text/javascript\">google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});google.setOnLoadCallback(drawChart);function drawChart() {var data = new google.visualization.DataTable();data.addColumn('string', 'Year');data.addColumn('number', 'Llegadas con exito');data.addColumn('number', 'Colisiones');data.addRows(["];
    
    int iteration = 0;
    
    for (NSTextCheckingResult *match in matches) {
        NSRange successRange = [match rangeAtIndex:1];
        NSRange collisionRange = [match rangeAtIndex:2];
        
        NSString *successString = [matrixString substringWithRange:successRange];
        NSString *collisionString = [matrixString substringWithRange:collisionRange];
        
        if (iteration != 0) {
            [outputString appendFormat:@","];
        }
        
        [outputString appendFormat:@"['%d',%d,%d]",iteration, [collisionString intValue], [successString intValue]];
        iteration++;
    }
    
    [outputString appendFormat:@"]);var chart = new google.visualization.AreaChart(document.getElementById('chart_div'));chart.draw(data, {width: 1000, height: 640, title: 'Performance',hAxis: {title: 'Iteracion',titleTextStyle: {color: '#FF0000'}}});}</script></head><body><div id=\"chart_div\"></div></body></html>"];
    
    [[outputString dataUsingEncoding:NSUTF8StringEncoding] writeToFile:[asdAppDelegate pathInDocumentsDirectory:@"performanceGraph.html"] atomically:NO];
}

- (void)graphSpeed {
    NSString *pDatabasePath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:@"interpreter.txt"];
    NSData *matrixData = [NSData dataWithContentsOfFile:pDatabasePath];
    NSString *matrixString = [[NSString alloc] initWithData:matrixData encoding:NSUTF8StringEncoding];
    
    //NSLog(@"%@", matrixString);
    
    NSRegularExpression *regex = [NSRegularExpression 
                                  regularExpressionWithPattern:
                                  @"[\\d]+\\s[\\d\\.]+\\s[\\d]+\\s[\\d]+\\s[\\d]+\\s*[\\d\\.E]+\\s([\\d\\.]+)"
                                  options:NSRegularExpressionCaseInsensitive|NSRegularExpressionDotMatchesLineSeparators error:nil];
    
    NSArray *matches = [regex matchesInString:matrixString options:0 range:NSMakeRange(0, [matrixString length])];
    
    NSMutableString *outputString = [NSMutableString string];
    [outputString appendFormat:@"<html><head><script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script><script type=\"text/javascript\">google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});google.setOnLoadCallback(drawChart);function drawChart() {var data = new google.visualization.DataTable();data.addColumn('string', 'Year');data.addColumn('number', 'Velocidad');data.addRows(["];
    
    int iteration = 0;
    
    for (NSTextCheckingResult *match in matches) {
        NSRange speedRange = [match rangeAtIndex:1];

        NSString *speedString = [matrixString substringWithRange:speedRange];
        
        if ((iteration % 10 == 0)) {
            if (iteration != 0) {
                [outputString appendFormat:@","];
            }
            [outputString appendFormat:@"['%d',%f]",iteration, [speedString floatValue] * 2 + 0.04];
        }
        
        iteration++;
    }
    
    [outputString appendFormat:@"]);var chart = new google.visualization.AreaChart(document.getElementById('chart_div'));chart.draw(data, {width: 1000, height: 640, title: 'Velocidad',hAxis: {title: 'Iteracion',titleTextStyle: {color: '#FF0000'}},vAxis: {minValue: 0, maxValue: 0.5}});}</script></head><body><div id=\"chart_div\"></div></body></html>"];
    
    [[outputString dataUsingEncoding:NSUTF8StringEncoding] writeToFile:[asdAppDelegate pathInDocumentsDirectory:@"speedGraph.html"] atomically:NO];
}

- (void)unparseQMatrix {
    NSString *pDatabasePath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:@"Matrix-parsed.txt"];
    NSData *matrixData = [NSData dataWithContentsOfFile:pDatabasePath];
    NSString *matrixString = [[NSString alloc] initWithData:matrixData encoding:NSUTF8StringEncoding];
    
    //NSLog(@"%@", matrixString);
    
    NSRegularExpression *regex = [NSRegularExpression 
                                  regularExpressionWithPattern:
                                  @"(\\[[^\\]]+])\\s\\+?(-?\\d+\\.\\d+)    \\+?([-\\+]?\\d+\\.\\d+)     \\+?(-?\\d+\\.\\d+)    \\+?(-?\\d+\\.\\d+)"
                                  options:NSRegularExpressionCaseInsensitive|NSRegularExpressionDotMatchesLineSeparators error:nil];
    
    NSArray *matches = [regex matchesInString:matrixString options:0 range:NSMakeRange(0, [matrixString length])];
    
    NSMutableString *outputString = [NSMutableString string];
    
    for (NSTextCheckingResult *match in matches) {
        NSRange sensorsRange = [match rangeAtIndex:1];
        NSRange leftRange = [match rangeAtIndex:2];
        NSRange rightRange = [match rangeAtIndex:3];
        NSRange noneRange = [match rangeAtIndex:4];
        NSRange backRange = [match rangeAtIndex:5];
        
        NSString *groupString = [matrixString substringWithRange:sensorsRange];
        NSString *leftString = [matrixString substringWithRange:leftRange];
        NSString *rightString = [matrixString substringWithRange:rightRange];
        NSString *noneString = [matrixString substringWithRange:noneRange];
        NSString *backString = [matrixString substringWithRange:backRange];
        
        [outputString appendFormat:@"%@ > ACTION_LEFT == %@\n", groupString, leftString];
        [outputString appendFormat:@"%@ > ACTION_RIGHT == %@\n", groupString, rightString];
        [outputString appendFormat:@"%@ > ACTION_NONE == %@\n", groupString, noneString];
        [outputString appendFormat:@"%@ > ACTION_BACK == %@\n", groupString, backString];
    }
    
    NSLog(@"%@", outputString);
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // Override point for customization after application launch.
    

    [self graphSpeed];
    [self graphSuccessVsCollisions];
    [self printFormattedQMatrix];
    
    self.window.rootViewController = self.viewController;
    [self.window makeKeyAndVisible];
    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    /*
     Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
     Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
     */
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    /*
     Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
     If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
     */
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    /*
     Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
     */
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    /*
     Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
     */
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    /*
     Called when the application is about to terminate.
     Save data if appropriate.
     See also applicationDidEnterBackground:.
     */
}

- (void)dealloc
{
    [_window release];
    [_viewController release];
    [super dealloc];
}

@end

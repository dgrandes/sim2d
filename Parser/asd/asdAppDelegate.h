//
//  asdAppDelegate.h
//  asd
//
//  Created by Matias Santiago Pan on 8/3/11.
//  Copyright 2011 Nasa Trained Monkeys. All rights reserved.
//

#import <UIKit/UIKit.h>

@class asdViewController;

@interface asdAppDelegate : NSObject <UIApplicationDelegate> {

}

@property (nonatomic, retain) IBOutlet UIWindow *window;

@property (nonatomic, retain) IBOutlet asdViewController *viewController;

@end

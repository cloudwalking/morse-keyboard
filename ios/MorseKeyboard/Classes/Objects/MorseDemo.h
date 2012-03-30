//
//  MorseDemo.h
//  MorseKeyboard
//
//  Created by David Patierno on 2/23/12.
//  Copyright (c) 2012 Google Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "Morse.h"

// Demo mode class. All button taps are converted into pre-defined signals so
// the user can just mash the button and write out morse code.
@interface MorseDemo : Morse
@property(nonatomic, copy) NSString* demoText;
@property(nonatomic, copy) NSString* demoSignals;
@property(nonatomic, assign) NSUInteger currentIndex;
@end

//
//  Morse.h
//  MorseKeyboard
//
//  Created by David Patierno on 2/22/12.
//  Copyright (c) 2012 Google Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Morse;

// Provides methods for communicating back to the delegate from timer events.
@protocol MorseDelegate <NSObject>
- (void)morse:(Morse*)morse updatedResultText:(NSString*)resultText;
- (void)morse:(Morse*)morse updatedPendingSignals:(NSString*)pendingSignals;
@end

// Translates tap events into proper Morse Code letters and spaces.
@interface Morse : NSObject
@property(nonatomic, assign) id<MorseDelegate> delegate;
@property(nonatomic, copy) NSString* resultText;
@property(nonatomic, copy) NSString* pendingSignals;
@property(nonatomic, retain) NSDictionary* signalTable;

- (id)initWithDelegate:(id<MorseDelegate>)delegate;
- (NSString*)signalsFromText:(NSString*)text;
- (void)reset;
- (void)tapBegin;
- (void)tapEnd;
@end

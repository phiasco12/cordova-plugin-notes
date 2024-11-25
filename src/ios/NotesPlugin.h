#import <Cordova/CDVPlugin.h>

@interface NotesPlugin : CDVPlugin

- (void)openNotesList:(CDVInvokedUrlCommand *)command;

@end

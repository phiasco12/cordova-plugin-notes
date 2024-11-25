#import "NotesPlugin.h"
#import "NotesListViewController.h" // Assumes a view controller for the notes list exists

@implementation NotesPlugin

- (void)openNotesList:(CDVInvokedUrlCommand *)command {
    // Create an instance of the NotesListViewController
    NotesListViewController *notesListVC = [[NotesListViewController alloc] init];
    
    // Present the NotesListViewController
    [self.viewController presentViewController:notesListVC animated:YES completion:nil];
    
    // Send success callback to JavaScript
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end

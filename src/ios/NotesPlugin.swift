import Foundation
import Cordova

@objc(NotesPlugin)
class NotesPlugin: CDVPlugin {

    @objc(openNotesList:)
    func openNotesList(command: CDVInvokedUrlCommand) {
        // Create an instance of the NotesListViewController
        let notesListVC = NotesListViewController()

        // Present the NotesListViewController
        self.viewController?.present(notesListVC, animated: true, completion: nil)

        // Send success callback to JavaScript
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK)
        self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
    }
}

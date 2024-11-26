import UIKit

@objc(NotesPlugin)
class NotesPlugin: CDVPlugin {

    @objc(openNotesList:)
    func openNotesList(command: CDVInvokedUrlCommand) {
        // Try to instantiate the NotesListViewController from the main storyboard
        guard let storyboard = UIStoryboard(name: "Main", bundle: nil) as UIStoryboard?,
              let notesListVC = storyboard.instantiateViewController(withIdentifier: "NotesListViewController") as? UIViewController else {
            // Return an error if the view controller cannot be instantiated
            let pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Failed to open notes list")
            self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
            return
        }

        // Present the NotesListViewController
        self.viewController?.present(notesListVC, animated: true, completion: nil)

        // Send success callback to JavaScript
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK)
        self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
    }
}

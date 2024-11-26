import UIKit

class NotesListViewController: UIViewController {

    // Properties
    var notesScrollView: UIScrollView!
    var savedNotes: [String] = []
    var notesDirectory: String!

    override func viewDidLoad() {
        super.viewDidLoad()

        // Set up the view
        view.backgroundColor = .white
        notesDirectory = getNotesDirectory()
        savedNotes = []

        // Create a "Create Note" button
        let createNoteButton = UIButton(type: .system)
        createNoteButton.setTitle("Create Note", for: .normal)
        createNoteButton.frame = CGRect(x: 20, y: 40, width: view.frame.size.width - 40, height: 50)
        createNoteButton.backgroundColor = UIColor(red: 0.85, green: 0.07, blue: 0.43, alpha: 1.0)
        createNoteButton.setTitleColor(.white, for: .normal)
        createNoteButton.layer.cornerRadius = 10
        createNoteButton.addTarget(self, action: #selector(openNoteCreator), for: .touchUpInside)
        view.addSubview(createNoteButton)

        // Set up the scroll view for notes
        notesScrollView = UIScrollView(frame: CGRect(x: 0, y: 100, width: view.frame.size.width, height: view.frame.size.height - 100))
        view.addSubview(notesScrollView)

        // Load saved notes
        loadSavedNotes()
    }

    // Get the path to the notes directory
    func getNotesDirectory() -> String {
        let paths = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)
        let documentsDirectory = paths.first!
        let notesDirectory = (documentsDirectory as NSString).appendingPathComponent("saved_notes")

        let fileManager = FileManager.default
        if !fileManager.fileExists(atPath: notesDirectory) {
            try? fileManager.createDirectory(atPath: notesDirectory, withIntermediateDirectories: true, attributes: nil)
        }
        return notesDirectory
    }

    // Load all saved notes and display them
    func loadSavedNotes() {
        savedNotes.removeAll()
        notesScrollView.subviews.forEach { $0.removeFromSuperview() }

        let fileManager = FileManager.default
        let files = (try? fileManager.contentsOfDirectory(atPath: notesDirectory)) ?? []

        var x: CGFloat = 20
        var y: CGFloat = 20
        let noteSize: CGFloat = 100

        for fileName in files {
            if fileName.hasSuffix(".png") {
                let notePath = (notesDirectory as NSString).appendingPathComponent(fileName)
                let jsonPath = (notePath as NSString).deletingPathExtension + ".json"

                if fileManager.fileExists(atPath: jsonPath) {
                    savedNotes.append(fileName)
                    addNoteToScrollView(noteFileName: fileName, at: CGPoint(x: x, y: y))
                    x += noteSize + 20

                    if x + noteSize > view.frame.size.width {
                        x = 20
                        y += noteSize + 20
                    }
                }
            }
        }
        notesScrollView.contentSize = CGSize(width: view.frame.size.width, height: y + noteSize + 20)
    }

    // Add a note to the scroll view
    func addNoteToScrollView(noteFileName: String, at position: CGPoint) {
        let notePath = (notesDirectory as NSString).appendingPathComponent(noteFileName)
        if let noteImage = UIImage(contentsOfFile: notePath) {
            let noteButton = UIButton(type: .custom)
            noteButton.frame = CGRect(x: position.x, y: position.y, width: 100, height: 100)
            noteButton.setImage(noteImage, for: .normal)
            noteButton.layer.cornerRadius = 10
            noteButton.clipsToBounds = true
            noteButton.backgroundColor = .lightGray
            noteButton.tag = savedNotes.firstIndex(of: noteFileName) ?? 0
            noteButton.addTarget(self, action: #selector(openNoteEditor(_:)), for: .touchUpInside)
            notesScrollView.addSubview(noteButton)
        }
    }

    // Open the note editor for a specific note
    @objc func openNoteEditor(_ sender: UIButton) {
        let noteFileName = savedNotes[sender.tag]
        let noteEditor = NoteEditorViewController()
        noteEditor.noteFileName = noteFileName // Assuming noteFileName is a property of NoteEditorViewController
        present(noteEditor, animated: true, completion: nil)
    }

    // Open the note creator
    @objc func openNoteCreator() {
        let noteEditor = NoteEditorViewController()
        present(noteEditor, animated: true) {
            // Optionally reload after note creation
            self.loadSavedNotes()
        }
    }
}

import UIKit

class NoteEditorViewController: UIViewController, UITextViewDelegate {

    // UI Elements
    private var scrollView: UIScrollView!
    private var pagesContainer: UIView!
    private var bottomToolbar: UIView!

    private var pageSize: CGSize = .zero
    private var pages: [UIView] = []
    private weak var activePage: UIView?
    private weak var activeTextView: UITextView?

    var noteFileName: String?

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white

        // Setup UI
        setupScrollView()
        setupBottomToolbar()

        // Add the first page automatically
        addNewPage()
    }

    // MARK: - UI Setup

    private func setupScrollView() {
        scrollView = UIScrollView(frame: view.bounds)
        scrollView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        view.addSubview(scrollView)

        pagesContainer = UIView(frame: scrollView.bounds)
        scrollView.addSubview(pagesContainer)
    }

    private func setupBottomToolbar() {
        bottomToolbar = UIView(frame: CGRect(x: 0, y: view.bounds.height - 60, width: view.bounds.width, height: 60))
        bottomToolbar.autoresizingMask = [.flexibleWidth, .flexibleTopMargin]
        bottomToolbar.backgroundColor = .darkGray

        // Save button
        let saveButton = UIButton(type: .system)
        saveButton.setTitle("Save", for: .normal)
        saveButton.frame = CGRect(x: 10, y: 10, width: 100, height: 40)
        saveButton.addTarget(self, action: #selector(saveAndReturn), for: .touchUpInside)
        bottomToolbar.addSubview(saveButton)

        view.addSubview(bottomToolbar)
    }

    // MARK: - Page Management

    private func addNewPage() {
        let pageWidth = view.bounds.width - 40 // 20px padding on each side
        let pageHeight = view.bounds.height - 120 // Leave space for toolbar
        let verticalSpacing: CGFloat = 20.0

        // Calculate Y offset for the new page
        let pageYPosition = pages.last.map { $0.frame.maxY + verticalSpacing } ?? 0

        // Create a new page container
        let page = UIView(frame: CGRect(x: 20, y: pageYPosition, width: pageWidth, height: pageHeight))
        page.backgroundColor = .white
        page.layer.borderColor = UIColor.lightGray.cgColor
        page.layer.borderWidth = 1.0
        page.layer.cornerRadius = 10.0

        // Add a UITextView for typing
        let textView = UITextView(frame: page.bounds.insetBy(dx: 10, dy: 10))
        textView.backgroundColor = .clear
        textView.font = .systemFont(ofSize: 16)
        textView.textColor = .black
        textView.delegate = self
        textView.autoresizingMask = [.flexibleWidth, .flexibleHeight]

        page.addSubview(textView)
        pagesContainer.addSubview(page)
        pages.append(page)

        activePage = page
        activeTextView = textView

        // Update container height to fit new page
        let newHeight = page.frame.maxY + verticalSpacing
        pagesContainer.frame.size.height = newHeight
        scrollView.contentSize = CGSize(width: scrollView.bounds.width, height: newHeight)

        // Automatically scroll to the new page
        scrollToPage(page)
    }

    private func scrollToPage(_ page: UIView) {
        let offset = page.frame.origin.y - 10.0
        scrollView.setContentOffset(CGPoint(x: 0, y: offset), animated: true)
    }

    // MARK: - Text Overflow Handling

    func textViewDidChange(_ textView: UITextView) {
        let textSize = textView.sizeThatFits(CGSize(width: textView.bounds.width, height: .greatestFiniteMagnitude))
        if textSize.height > textView.bounds.height {
            handleTextOverflow(from: textView)
        }
    }

    private func handleTextOverflow(from textView: UITextView) {
        guard let visibleRange = getVisibleTextRange(for: textView) else { return }

        let text = textView.text ?? ""
        let visibleText = String(text.prefix(visibleRange.location))
        let remainingText = String(text.suffix(text.count - visibleRange.location))

        textView.text = visibleText

        addNewPage()
        activeTextView?.text = remainingText
        activeTextView?.becomeFirstResponder()
    }

    private func getVisibleTextRange(for textView: UITextView) -> NSRange? {
        guard let start = textView.position(from: textView.beginningOfDocument, offset: 0),
              let end = textView.characterRange(at: CGPoint(x: 0, y: textView.bounds.height))?.end else {
            return nil
        }
        let startOffset = textView.offset(from: textView.beginningOfDocument, to: end)
        return NSRange(location: startOffset, length: textView.text.count - startOffset)
    }

    // MARK: - Save Functionality

    @objc private func saveAndReturn() {
        guard !pages.isEmpty else {
            dismiss(animated: true, completion: nil)
            return
        }

        let noteFileName = self.noteFileName ?? "note_\(Int(Date().timeIntervalSince1970))"
        let notesDir = notesDirectory()

        var pageDataArray: [[String: Any]] = []

        for page in pages {
            guard let textView = page.subviews.first as? UITextView else { continue }
            let textContent = textView.text ?? ""

            let pageData: [String: Any] = [
                "text": textContent,
                "sketch": [] // Placeholder for sketch data
            ]
            pageDataArray.append(pageData)
        }

        let noteData: [String: Any] = ["pages": pageDataArray]
        if let jsonData = try? JSONSerialization.data(withJSONObject: noteData, options: .prettyPrinted) {
            let jsonPath = notesDir.appendingPathComponent("\(noteFileName).json")
            try? jsonData.write(to: URL(fileURLWithPath: jsonPath))
        }

        // Save preview image of the first page
        if let firstPage = pages.first {
            let bitmapPath = notesDir.appendingPathComponent("\(noteFileName).png")
            UIGraphicsBeginImageContextWithOptions(firstPage.bounds.size, false, UIScreen.main.scale)
            firstPage.layer.render(in: UIGraphicsGetCurrentContext()!)
            let bitmap = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            try? bitmap?.pngData()?.write(to: URL(fileURLWithPath: bitmapPath))
        }

        dismiss(animated: true, completion: nil)
    }

    // MARK: - Utility

    private func notesDirectory() -> String {
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        let notesDir = paths[0].appendingPathComponent("saved_notes")

        if !FileManager.default.fileExists(atPath: notesDir.path) {
            try? FileManager.default.createDirectory(at: notesDir, withIntermediateDirectories: true, attributes: nil)
        }
        return notesDir.path
    }
}

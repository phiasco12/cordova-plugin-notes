import UIKit

class NoteEditorViewController: UIViewController, UITextViewDelegate {

    // UI Elements
    var scrollView: UIScrollView!
    var pagesContainer: UIView!
    var bottomToolbar: UIView!

    var pageSize: CGSize = .zero
    var pages: [UIView] = []
    weak var activePage: UIView?
    weak var activeTextView: UITextView?
    var activeDrawingLayer: CAShapeLayer?
    var isDrawingMode: Bool = false

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white

        // Setup scroll view and toolbar
        setupScrollView()
        setupBottomToolbar()

        // Add the first page
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
        let toolbarHeight: CGFloat = 60
        bottomToolbar = UIView(frame: CGRect(x: 0, y: view.bounds.height - toolbarHeight, width: view.bounds.width, height: toolbarHeight))
        bottomToolbar.autoresizingMask = [.flexibleWidth, .flexibleTopMargin]
        bottomToolbar.backgroundColor = .darkGray
        view.addSubview(bottomToolbar)

        // Save button
        let saveButton = UIButton(type: .system)
        saveButton.setTitle("Save", for: .normal)
        saveButton.frame = CGRect(x: 10, y: 10, width: 100, height: 40)
        saveButton.addTarget(self, action: #selector(saveAndReturn), for: .touchUpInside)
        bottomToolbar.addSubview(saveButton)

        // Toggle mode button
        let toggleModeButton = UIButton(type: .system)
        toggleModeButton.setTitle("Toggle Mode", for: .normal)
        toggleModeButton.frame = CGRect(x: view.bounds.width - 110, y: 10, width: 100, height: 40)
        toggleModeButton.addTarget(self, action: #selector(toggleTypingDrawingMode), for: .touchUpInside)
        bottomToolbar.addSubview(toggleModeButton)
    }

    // MARK: - Page Management
    private func addNewPage() {
        let pageWidth = view.bounds.width - 40 // 20px padding on each side
        let pageHeight = view.bounds.height - 120 // Leave space for toolbar
        let verticalSpacing: CGFloat = 20.0

        // Calculate Y offset for the new page
        let pageYPosition: CGFloat = pages.last.map { $0.frame.maxY + verticalSpacing } ?? 0

        // Create a new page container
        let page = UIView(frame: CGRect(x: 20, y: pageYPosition, width: pageWidth, height: pageHeight))
        page.backgroundColor = .white
        page.layer.borderColor = UIColor.lightGray.cgColor
        page.layer.borderWidth = 1.0
        page.layer.cornerRadius = 10.0

        // Add a UITextView for typing
        let textView = UITextView(frame: page.bounds.insetBy(dx: 10, dy: 10))
        textView.backgroundColor = .clear
        textView.font = UIFont.systemFont(ofSize: 16.0)
        textView.textColor = .black
        textView.delegate = self
        textView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        page.addSubview(textView)

        // Add the drawing layer
        let drawingLayer = CAShapeLayer()
        drawingLayer.frame = page.bounds
        drawingLayer.backgroundColor = UIColor.clear.cgColor
        drawingLayer.lineWidth = 2.0
        drawingLayer.strokeColor = UIColor.black.cgColor
        drawingLayer.fillColor = UIColor.clear.cgColor
        page.layer.addSublayer(drawingLayer)

        // Add pan gesture for drawing
        let panGesture = UIPanGestureRecognizer(target: self, action: #selector(handleDrawingPan(_:)))
        page.addGestureRecognizer(panGesture)

        // Add the page to the container
        pagesContainer.addSubview(page)
        pages.append(page)

        // Update active elements
        activePage = page
        activeTextView = textView
        activeDrawingLayer = drawingLayer

        // Update the height of the pagesContainer to fit the new page
        let newHeight = page.frame.maxY + verticalSpacing
        pagesContainer.frame = CGRect(x: 0, y: 0, width: scrollView.bounds.width, height: newHeight)
        scrollView.contentSize = CGSize(width: scrollView.bounds.width, height: newHeight)

        // Automatically scroll to the new page
        scrollToPage(page)
    }

    private func scrollToPage(_ page: UIView) {
        let offset = CGPoint(x: 0, y: page.frame.origin.y - 10.0) // Small padding before the page
        scrollView.setContentOffset(offset, animated: true)
    }

    // MARK: - Drawing and Typing Mode
    @objc private func toggleTypingDrawingMode() {
        isDrawingMode.toggle()

        // Toggle interaction for UITextView and drawing layer
        activeTextView?.isUserInteractionEnabled = !isDrawingMode
        activeDrawingLayer?.isHidden = !isDrawingMode
    }

    @objc private func handleDrawingPan(_ gesture: UIPanGestureRecognizer) {
        guard isDrawingMode, let drawingLayer = activeDrawingLayer else { return }

        let currentPoint = gesture.location(in: activePage)

        if gesture.state == .began {
            let path = UIBezierPath()
            path.lineWidth = 2.0
            path.move(to: currentPoint)
            drawingLayer.path = path.cgPath
        } else if gesture.state == .changed {
            guard let currentPath = drawingLayer.path else { return }
            let path = UIBezierPath(cgPath: currentPath)
            path.addLine(to: currentPoint)
            drawingLayer.path = path.cgPath
        }
    }

    // MARK: - Save Functionality
    @objc private func saveAndReturn() {
        // Implement your save functionality here
        print("Save functionality not implemented.")
    }
}

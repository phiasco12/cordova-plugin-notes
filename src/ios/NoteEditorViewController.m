#import "NoteEditorViewController.h"

@interface NoteEditorViewController ()

// UI Elements
@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) UIView *pagesContainer;
@property (nonatomic, strong) UIView *bottomToolbar;

@property (nonatomic, strong) UITextView *activeTextView; // Text input for active page
@property (nonatomic, strong) UIImageView *activeSketchView; // Sketch input for active page
@property (nonatomic) BOOL isDrawingMode; // Track text/drawing mode

@property (nonatomic, strong) NSMutableArray<UIView *> *pages; // Pages in the editor
@property (nonatomic, weak) UIView *activePage; // Current active page

@end

@implementation NoteEditorViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];

    // Initialize properties
    self.pages = [NSMutableArray array];
    self.isDrawingMode = NO;

    // Setup scroll view
    [self setupScrollView];

    // Add the first page
    [self addNewPage];

    // Setup bottom toolbar
    [self setupBottomToolbar];
}

#pragma mark - UI Setup

- (void)setupScrollView {
    self.scrollView = [[UIScrollView alloc] initWithFrame:self.view.bounds];
    self.scrollView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    [self.view addSubview:self.scrollView];

    self.pagesContainer = [[UIView alloc] initWithFrame:self.scrollView.bounds];
    [self.scrollView addSubview:self.pagesContainer];
}

- (void)setupBottomToolbar {
    self.bottomToolbar = [[UIView alloc] initWithFrame:CGRectMake(0, self.view.bounds.size.height - 60, self.view.bounds.size.width, 60)];
    self.bottomToolbar.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleTopMargin;
    self.bottomToolbar.backgroundColor = [UIColor darkGrayColor];

    // Save Button
    UIButton *saveButton = [self createToolbarButtonWithTitle:@"Save"];
    saveButton.frame = CGRectMake(10, 10, 80, 40);
    [saveButton addTarget:self action:@selector(saveAndReturn) forControlEvents:UIControlEventTouchUpInside];
    [self.bottomToolbar addSubview:saveButton];

    // Toggle Text/Draw Mode Button
    UIButton *toggleModeButton = [self createToolbarButtonWithTitle:@"Toggle"];
    toggleModeButton.frame = CGRectMake(100, 10, 80, 40);
    [toggleModeButton addTarget:self action:@selector(toggleDrawingMode) forControlEvents:UIControlEventTouchUpInside];
    [self.bottomToolbar addSubview:toggleModeButton];

    // Add Page Button
    UIButton *addPageButton = [self createToolbarButtonWithTitle:@"Add Page"];
    addPageButton.frame = CGRectMake(190, 10, 120, 40);
    [addPageButton addTarget:self action:@selector(addNewPage) forControlEvents:UIControlEventTouchUpInside];
    [self.bottomToolbar addSubview:addPageButton];

    [self.view addSubview:self.bottomToolbar];
}

- (UIButton *)createToolbarButtonWithTitle:(NSString *)title {
    UIButton *button = [UIButton buttonWithType:UIButtonTypeSystem];
    [button setTitle:title forState:UIControlStateNormal];
    button.backgroundColor = [UIColor lightGrayColor];
    button.layer.cornerRadius = 5.0;
    [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    return button;
}

#pragma mark - Page Management

- (void)addNewPage {
    CGFloat pageWidth = self.view.bounds.size.width - 40; // 20px padding on each side
    CGFloat pageHeight = self.view.bounds.size.height - 120; // Leave space for toolbar

    UIView *page = [[UIView alloc] initWithFrame:CGRectMake(20, self.pages.count * (pageHeight + 20), pageWidth, pageHeight)];
    page.backgroundColor = [UIColor whiteColor];
    page.layer.borderColor = [UIColor lightGrayColor].CGColor;
    page.layer.borderWidth = 1.0;
    page.layer.cornerRadius = 10.0;

    // Add text view for typing
    UITextView *textView = [[UITextView alloc] initWithFrame:page.bounds];
    textView.backgroundColor = [UIColor clearColor];
    textView.font = [UIFont systemFontOfSize:16.0];
    textView.textColor = [UIColor blackColor];
    [page addSubview:textView];

    // Add image view for drawing (overlay)
    UIImageView *sketchView = [[UIImageView alloc] initWithFrame:page.bounds];
    sketchView.backgroundColor = [UIColor clearColor];
    sketchView.userInteractionEnabled = YES; // Enable touch events
    [page addSubview:sketchView];

    // Add gesture for drawing
    UIPanGestureRecognizer *panGesture = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(handlePanGesture:)];
    [sketchView addGestureRecognizer:panGesture];

    // Add the page to the container
    [self.pagesContainer addSubview:page];
    [self.pages addObject:page];

    self.activePage = page;
    self.activeTextView = textView;
    self.activeSketchView = sketchView;

    // Update scroll content size
    self.pagesContainer.frame = CGRectMake(0, 0, self.scrollView.bounds.size.width, CGRectGetMaxY(page.frame) + 20);
    self.scrollView.contentSize = self.pagesContainer.frame.size;
}

#pragma mark - Drawing and Typing

- (void)toggleDrawingMode {
    self.isDrawingMode = !self.isDrawingMode;

    if (self.isDrawingMode) {
        self.activeTextView.hidden = YES; // Hide text view
        self.activeSketchView.hidden = NO; // Show sketch view
    } else {
        self.activeTextView.hidden = NO; // Show text view
        self.activeSketchView.hidden = YES; // Hide sketch view
    }
}

- (void)handlePanGesture:(UIPanGestureRecognizer *)gesture {
    if (!self.isDrawingMode) return;

    CGPoint touchPoint = [gesture locationInView:self.activeSketchView];

    UIGraphicsBeginImageContext(self.activeSketchView.frame.size);
    [self.activeSketchView.image drawInRect:self.activeSketchView.bounds];

    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetStrokeColorWithColor(context, [UIColor blackColor].CGColor);
    CGContextSetLineWidth(context, 2.0);

    if (gesture.state == UIGestureRecognizerStateBegan) {
        CGContextMoveToPoint(context, touchPoint.x, touchPoint.y);
    } else if (gesture.state == UIGestureRecognizerStateChanged) {
        CGContextAddLineToPoint(context, touchPoint.x, touchPoint.y);
        CGContextStrokePath(context);
        CGContextMoveToPoint(context, touchPoint.x, touchPoint.y);
    }

    self.activeSketchView.image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
}

#pragma mark - Save Functionality

- (void)saveAndReturn {
    // Save text and sketches for each page
    NSMutableArray *pagesData = [NSMutableArray array];
    for (UIView *page in self.pages) {
        UITextView *textView = page.subviews[0];
        UIImageView *sketchView = page.subviews[1];

        NSMutableDictionary *pageData = [NSMutableDictionary dictionary];
        pageData[@"text"] = textView.text ?: @"";

        if (sketchView.image) {
            NSString *imagePath = [self saveImage:sketchView.image];
            if (imagePath) pageData[@"sketch"] = imagePath;
        }

        [pagesData addObject:pageData];
    }

    NSLog(@"Saved Pages: %@", pagesData);
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (NSString *)saveImage:(UIImage *)image {
    NSData *imageData = UIImagePNGRepresentation(image);
    NSString *filePath = [NSTemporaryDirectory() stringByAppendingPathComponent:[NSString stringWithFormat:@"sketch_%@.png", @([[NSDate date] timeIntervalSince1970])]];
    if ([imageData writeToFile:filePath atomically:YES]) {
        return filePath;
    }
    return nil;
}

@end

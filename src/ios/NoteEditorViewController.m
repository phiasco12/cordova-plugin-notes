#import "NoteEditorViewController.h"

@interface SketchView : UIView

@property (nonatomic, strong) NSMutableArray<UIBezierPath *> *paths; // All paths for drawing
@property (nonatomic, strong) UIBezierPath *currentPath; // Current drawing path
@property (nonatomic, strong) UIColor *lineColor;
@property (nonatomic, assign) CGFloat lineWidth;

- (void)clearDrawing;
- (NSArray *)getDrawingPaths;

@end

@implementation SketchView

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.paths = [NSMutableArray array];
        self.lineColor = [UIColor blackColor];
        self.lineWidth = 2.0;
        self.backgroundColor = [UIColor clearColor];
    }
    return self;
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch locationInView:self];
    self.currentPath = [UIBezierPath bezierPath];
    self.currentPath.lineWidth = self.lineWidth;
    [self.currentPath moveToPoint:point];
    [self.paths addObject:self.currentPath];
    [self setNeedsDisplay];
}

- (void)touchesMoved:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch locationInView:self];
    [self.currentPath addLineToPoint:point];
    [self setNeedsDisplay];
}

- (void)drawRect:(CGRect)rect {
    for (UIBezierPath *path in self.paths) {
        [self.lineColor setStroke];
        [path stroke];
    }
}

- (void)clearDrawing {
    [self.paths removeAllObjects];
    [self setNeedsDisplay];
}

- (NSArray *)getDrawingPaths {
    NSMutableArray *pathArray = [NSMutableArray array];
    for (UIBezierPath *path in self.paths) {
        NSMutableArray *points = [NSMutableArray array];
        CGPathApply(path.CGPath, (__bridge void *)points, CGPathApplier);
        [pathArray addObject:points];
    }
    return pathArray;
}

@end

@interface NoteEditorViewController () <UITextViewDelegate>

// UI Elements
@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) UIView *pagesContainer;
@property (nonatomic, strong) UIView *bottomToolbar;

@property (nonatomic) CGSize pageSize;
@property (nonatomic, strong) NSMutableArray<UIView *> *pages; // Pages in the editor
@property (nonatomic, weak) UIView *activePage; // Current active page
@property (nonatomic, weak) UITextView *activeTextView; // Current active text input
@property (nonatomic, assign) BOOL isDrawingMode; // Track whether in drawing mode

@end

@implementation NoteEditorViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    self.isDrawingMode = NO;

    // Initialize properties
    self.pages = [NSMutableArray array];
    self.pageSize = CGSizeZero;

    // Setup scroll view
    [self setupScrollView];

    // Setup bottom toolbar
    [self setupBottomToolbar];

    // Add the first page automatically
    [self addNewPage];
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

    // Save button
    UIButton *saveButton = [UIButton buttonWithType:UIButtonTypeSystem];
    [saveButton setTitle:@"Save" forState:UIControlStateNormal];
    [saveButton setFrame:CGRectMake(10, 10, 100, 40)];
    [saveButton addTarget:self action:@selector(saveAndReturn) forControlEvents:UIControlEventTouchUpInside];
    [self.bottomToolbar addSubview:saveButton];

    // Toggle Sketch Mode button
    UIButton *toggleButton = [UIButton buttonWithType:UIButtonTypeSystem];
    [toggleButton setTitle:@"Sketch" forState:UIControlStateNormal];
    [toggleButton setFrame:CGRectMake(120, 10, 100, 40)];
    [toggleButton addTarget:self action:@selector(toggleDrawingMode) forControlEvents:UIControlEventTouchUpInside];
    [self.bottomToolbar addSubview:toggleButton];

    // Add toolbar to the view
    [self.view addSubview:self.bottomToolbar];
}

#pragma mark - Page Management

- (void)addNewPage {
    CGFloat pageWidth = self.view.bounds.size.width - 40; // 20px padding on each side
    CGFloat pageHeight = self.view.bounds.size.height - 120; // Leave space for toolbar
    CGFloat verticalSpacing = 20.0; // Space between pages

    // Calculate the Y offset for the new page
    CGFloat pageYPosition = 0;

    if (self.pages.count > 0) {
        // Get the position of the last page and add spacing
        UIView *lastPage = [self.pages lastObject];
        pageYPosition = CGRectGetMaxY(lastPage.frame) + verticalSpacing;
    }

    // Create a new page container
    UIView *page = [[UIView alloc] initWithFrame:CGRectMake(20, pageYPosition, pageWidth, pageHeight)];
    page.backgroundColor = [UIColor whiteColor];
    page.layer.borderColor = [UIColor lightGrayColor].CGColor;
    page.layer.borderWidth = 1.0;
    page.layer.cornerRadius = 10.0;

    // Add a UITextView for typing
    UITextView *textView = [[UITextView alloc] initWithFrame:CGRectInset(page.bounds, 10, 10)];
    textView.backgroundColor = [UIColor clearColor];
    textView.font = [UIFont systemFontOfSize:16.0];
    textView.textColor = [UIColor blackColor];
    textView.delegate = self; // Enable overflow handling
    textView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;

    // Add a SketchView for drawing
    SketchView *sketchView = [[SketchView alloc] initWithFrame:page.bounds];
    sketchView.hidden = YES; // Initially hidden
    [page addSubview:sketchView];

    // Add the UITextView and SketchView to the page
    [page addSubview:textView];
    [page addSubview:sketchView];

    // Add the page to the container
    [self.pagesContainer addSubview:page];
    [self.pages addObject:page];

    self.activePage = page;
    self.activeTextView = textView;

    // Update the height of the pagesContainer to fit the new page
    CGFloat newHeight = CGRectGetMaxY(page.frame) + verticalSpacing;
    self.pagesContainer.frame = CGRectMake(0, 0, self.scrollView.bounds.size.width, newHeight);
    self.scrollView.contentSize = CGSizeMake(self.scrollView.bounds.size.width, newHeight);

    // Automatically scroll to the new page
    [self scrollToPage:page];
}

#pragma mark - Toggle Drawing Mode

- (void)toggleDrawingMode {
    self.isDrawingMode = !self.isDrawingMode;

    for (UIView *page in self.pages) {
        UITextView *textView = page.subviews[0];
        SketchView *sketchView = page.subviews[1];

        textView.hidden = self.isDrawingMode;
        sketchView.hidden = !self.isDrawingMode;
    }
}

#pragma mark - Save Functionality

// Same save function as before, now also save the sketch data

@end

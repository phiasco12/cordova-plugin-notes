/*#import "NoteEditorViewController.h"

@interface NoteEditorViewController () <UITextViewDelegate>

// UI Elements
@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) UIView *pagesContainer;
@property (nonatomic, strong) UIView *bottomToolbar;

@property (nonatomic) CGSize pageSize;
@property (nonatomic, strong) NSMutableArray<UIView *> *pages; // Pages in the editor
@property (nonatomic, weak) UIView *activePage; // Current active page
@property (nonatomic, weak) UITextView *activeTextView; // Current active text input

@end

@implementation NoteEditorViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];

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

    // Add the UITextView to the page
    [page addSubview:textView];

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

- (void)scrollToPage:(UIView *)page {
    CGFloat offset = page.frame.origin.y - 10.0; // Small padding before the page
    [self.scrollView setContentOffset:CGPointMake(0, offset) animated:YES];
}

#pragma mark - Text Overflow Handling

- (void)textViewDidChange:(UITextView *)textView {
    // Check if the text exceeds the height of the current page
    CGSize textSize = [textView sizeThatFits:CGSizeMake(textView.bounds.size.width, CGFLOAT_MAX)];
    if (textSize.height > textView.bounds.size.height) {
        // Move the overflowing text to a new page
        [self handleTextOverflowFromTextView:textView];
    }
}

- (void)handleTextOverflowFromTextView:(UITextView *)textView {
    // Get the layout of the text
    NSRange visibleRange = [self getVisibleTextRangeForTextView:textView];
    if (visibleRange.location == NSNotFound) return;

    // Extract the overflowing text
    NSString *text = textView.text;
    NSString *visibleText = [text substringToIndex:visibleRange.location];
    NSString *remainingText = [text substringFromIndex:visibleRange.location];

    // Update the current page with visible text
    textView.text = visibleText;

    // Create a new page and set the remaining text
    [self addNewPage];
    self.activeTextView.text = remainingText;

    // Move the cursor to the new page
    [self.activeTextView becomeFirstResponder];
}

- (NSRange)getVisibleTextRangeForTextView:(UITextView *)textView {
    UITextPosition *startPosition = textView.beginningOfDocument;
    UITextPosition *endPosition = [textView characterRangeAtPoint:CGPointMake(0, textView.bounds.size.height)].end;
    if (!startPosition || !endPosition) return NSMakeRange(NSNotFound, 0);

    NSInteger startOffset = [textView offsetFromPosition:startPosition toPosition:endPosition];
    return NSMakeRange(startOffset, textView.text.length - startOffset);
}

#pragma mark - Save Functionality

- (void)saveAndReturn {
    if (self.pages.count == 0) {
        [self dismissViewControllerAnimated:YES completion:nil];
        return;
    }

    // Generate note file name
    NSString *noteFileName = self.noteFileName ?: [NSString stringWithFormat:@"note_%@", @([[NSDate date] timeIntervalSince1970])];
    NSString *notesDir = [self notesDirectory];

    // Prepare to save data
    NSMutableArray *pageDataArray = [NSMutableArray array];

    for (UIView *page in self.pages) {
        UITextView *textView = page.subviews[0]; // Get the text view
        NSString *textContent = textView.text ?: @"";

        NSDictionary *pageData = @{
            @"text": textContent,
            @"sketch": @[] // Placeholder for sketch data
        };
        [pageDataArray addObject:pageData];
    }

    NSDictionary *noteData = @{@"pages": pageDataArray};
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:noteData options:NSJSONWritingPrettyPrinted error:nil];

    // Save the JSON data
    NSString *jsonPath = [notesDir stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.json", noteFileName]];
    BOOL jsonSaved = [jsonData writeToFile:jsonPath atomically:YES];

    if (!jsonSaved) {
        NSLog(@"Error saving JSON file.");
        return;
    }

    // Save a preview image of the first page
    NSString *bitmapPath = [notesDir stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.png", noteFileName]];
    UIView *firstPage = self.pages.firstObject;

    UIGraphicsBeginImageContextWithOptions(firstPage.bounds.size, NO, [UIScreen mainScreen].scale);
    [firstPage.layer renderInContext:UIGraphicsGetCurrentContext()];
    UIImage *bitmap = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();

    BOOL imageSaved = [UIImagePNGRepresentation(bitmap) writeToFile:bitmapPath atomically:YES];

    if (!imageSaved) {
        NSLog(@"Error saving preview image.");
        return;
    }

    // Return to the previous screen
    [self dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - Utility

- (NSString *)notesDirectory {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths firstObject];
    NSString *notesDir = [documentsDirectory stringByAppendingPathComponent:@"saved_notes"];

    if (![[NSFileManager defaultManager] fileExistsAtPath:notesDir]) {
        [[NSFileManager defaultManager] createDirectoryAtPath:notesDir withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return notesDir;
}

@end*/




















/*#import "NoteEditorViewController.h"

@interface NoteEditorViewController () <UITextViewDelegate>

// UI Elements
@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) UIView *pagesContainer;
@property (nonatomic, strong) UIView *bottomToolbar;

@property (nonatomic) CGSize pageSize;
@property (nonatomic, strong) NSMutableArray<UIView *> *pages; // Pages in the editor
@property (nonatomic, weak) UIView *activePage; // Current active page
@property (nonatomic, weak) UITextView *activeTextView; // Current active text input

@end

@implementation NoteEditorViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];

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

    // Add the UITextView to the page
    [page addSubview:textView];

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

- (void)scrollToPage:(UIView *)page {
    CGFloat offset = page.frame.origin.y - 10.0; // Small padding before the page
    [self.scrollView setContentOffset:CGPointMake(0, offset) animated:YES];
}

#pragma mark - Text Overflow Handling

- (void)textViewDidChange:(UITextView *)textView {
    // Check if the text exceeds the height of the current page
    CGSize textSize = [textView sizeThatFits:CGSizeMake(textView.bounds.size.width, CGFLOAT_MAX)];
    if (textSize.height > textView.bounds.size.height) {
        // Move the overflowing text to a new page
        [self handleTextOverflowFromTextView:textView];
    }
}

- (void)handleTextOverflowFromTextView:(UITextView *)textView {
    // Get the layout of the text
    NSRange visibleRange = [self getVisibleTextRangeForTextView:textView];
    if (visibleRange.location == NSNotFound) return;

    // Extract the overflowing text
    NSString *text = textView.text;
    NSString *visibleText = [text substringToIndex:visibleRange.location];
    NSString *remainingText = [text substringFromIndex:visibleRange.location];

    // Update the current page with visible text
    textView.text = visibleText;

    // Create a new page and set the remaining text
    [self addNewPage];
    self.activeTextView.text = remainingText;

    // Move the cursor to the new page
    [self.activeTextView becomeFirstResponder];
}

- (NSRange)getVisibleTextRangeForTextView:(UITextView *)textView {
    UITextPosition *startPosition = textView.beginningOfDocument;
    UITextPosition *endPosition = [textView characterRangeAtPoint:CGPointMake(0, textView.bounds.size.height)].end;
    if (!startPosition || !endPosition) return NSMakeRange(NSNotFound, 0);

    NSInteger startOffset = [textView offsetFromPosition:startPosition toPosition:endPosition];
    return NSMakeRange(startOffset, textView.text.length - startOffset);
}

#pragma mark - Save Functionality

- (void)saveAndReturn {
    if (self.pages.count == 0) {
        [self dismissViewControllerAnimated:YES completion:nil];
        return;
    }

    // Generate note file name
    NSString *noteFileName = self.noteFileName ?: [NSString stringWithFormat:@"note_%@", @([[NSDate date] timeIntervalSince1970])];
    NSString *notesDir = [self notesDirectory];

    // Prepare to save data
    NSMutableArray *pageDataArray = [NSMutableArray array];

    for (UIView *page in self.pages) {
        UITextView *textView = page.subviews[0]; // Get the text view
        NSString *textContent = textView.text ?: @"";

        NSDictionary *pageData = @{
            @"text": textContent,
            @"sketch": @[] // Placeholder for sketch data
        };
        [pageDataArray addObject:pageData];
    }

    NSDictionary *noteData = @{@"pages": pageDataArray};
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:noteData options:NSJSONWritingPrettyPrinted error:nil];

    // Save the JSON data
    NSString *jsonPath = [notesDir stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.json", noteFileName]];
    BOOL jsonSaved = [jsonData writeToFile:jsonPath atomically:YES];

    if (!jsonSaved) {
        NSLog(@"Error saving JSON file.");
        return;
    }

    // Save a preview image of the first page
    NSString *bitmapPath = [notesDir stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.png", noteFileName]];
    UIView *firstPage = self.pages.firstObject;

    UIGraphicsBeginImageContextWithOptions(firstPage.bounds.size, NO, [UIScreen mainScreen].scale);
    [firstPage.layer renderInContext:UIGraphicsGetCurrentContext()];
    UIImage *bitmap = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();

    BOOL imageSaved = [UIImagePNGRepresentation(bitmap) writeToFile:bitmapPath atomically:YES];

    if (!imageSaved) {
        NSLog(@"Error saving preview image.");
        return;
    }

    // Return to the previous screen
    [self dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - Utility

- (NSString *)notesDirectory {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths firstObject];
    NSString *notesDir = [documentsDirectory stringByAppendingPathComponent:@"saved_notes"];

    if (![[NSFileManager defaultManager] fileExistsAtPath:notesDir]) {
        [[NSFileManager defaultManager] createDirectoryAtPath:notesDir withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return notesDir;
}

@end*/




















#import "NoteEditorViewController.h"

@interface SketchView : UIView

@property (nonatomic, strong) NSMutableArray<UIBezierPath *> *paths;
@property (nonatomic, strong) UIBezierPath *currentPath;
@property (nonatomic, strong) UIColor *lineColor;
@property (nonatomic, assign) CGFloat lineWidth;

- (void)clearDrawing;
- (NSArray<NSDictionary *> *)getDrawingPaths;

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

- (NSArray<NSDictionary *> *)getDrawingPaths {
    NSMutableArray *result = [NSMutableArray array];
    for (UIBezierPath *path in self.paths) {
        [result addObject:@{@"path": path}]; // Simplified representation
    }
    return result;
}

@end

@interface NoteEditorViewController () <UITextViewDelegate>

@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) UIView *pagesContainer;
@property (nonatomic, strong) UIView *bottomToolbar;

@property (nonatomic) CGSize pageSize;
@property (nonatomic, strong) NSMutableArray<UIView *> *pages;
@property (nonatomic, weak) UIView *activePage;
@property (nonatomic, weak) UITextView *activeTextView;
@property (nonatomic, assign) BOOL isDrawingMode;

@end

@implementation NoteEditorViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    self.isDrawingMode = NO;

    self.pages = [NSMutableArray array];
    self.pageSize = CGSizeZero;

    [self setupScrollView];
    [self setupBottomToolbar];
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

    UIButton *saveButton = [UIButton buttonWithType:UIButtonTypeSystem];
    [saveButton setTitle:@"Save" forState:UIControlStateNormal];
    [saveButton setFrame:CGRectMake(10, 10, 100, 40)];
    [saveButton addTarget:self action:@selector(saveAndReturn) forControlEvents:UIControlEventTouchUpInside];
    [self.bottomToolbar addSubview:saveButton];

    UIButton *toggleButton = [UIButton buttonWithType:UIButtonTypeSystem];
    [toggleButton setTitle:@"Toggle Drawing" forState:UIControlStateNormal];
    [toggleButton setFrame:CGRectMake(120, 10, 150, 40)];
    [toggleButton addTarget:self action:@selector(toggleDrawingMode) forControlEvents:UIControlEventTouchUpInside];
    [self.bottomToolbar addSubview:toggleButton];

    [self.view addSubview:self.bottomToolbar];
}

#pragma mark - Page Management

- (void)addNewPage {
    CGFloat pageWidth = self.view.bounds.size.width - 40;
    CGFloat pageHeight = self.view.bounds.size.height - 120;
    CGFloat verticalSpacing = 20.0;

    CGFloat pageYPosition = 0;
    if (self.pages.count > 0) {
        UIView *lastPage = [self.pages lastObject];
        pageYPosition = CGRectGetMaxY(lastPage.frame) + verticalSpacing;
    }

    UIView *page = [[UIView alloc] initWithFrame:CGRectMake(20, pageYPosition, pageWidth, pageHeight)];
    page.backgroundColor = [UIColor whiteColor];
    page.layer.borderColor = [UIColor lightGrayColor].CGColor;
    page.layer.borderWidth = 1.0;
    page.layer.cornerRadius = 10.0;

    UITextView *textView = [[UITextView alloc] initWithFrame:CGRectInset(page.bounds, 10, 10)];
    textView.backgroundColor = [UIColor clearColor];
    textView.font = [UIFont systemFontOfSize:16.0];
    textView.textColor = [UIColor blackColor];
    textView.delegate = self;
    textView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;

    SketchView *drawingView = [[SketchView alloc] initWithFrame:page.bounds];
    drawingView.hidden = YES;
    [page addSubview:textView];
    [page addSubview:drawingView];

    [self.pagesContainer addSubview:page];
    [self.pages addObject:page];

    self.activePage = page;
    self.activeTextView = textView;

    CGFloat newHeight = CGRectGetMaxY(page.frame) + verticalSpacing;
    self.pagesContainer.frame = CGRectMake(0, 0, self.scrollView.bounds.size.width, newHeight);
    self.scrollView.contentSize = CGSizeMake(self.scrollView.bounds.size.width, newHeight);

    [self scrollToPage:page];
}

- (void)scrollToPage:(UIView *)page {
    CGFloat offset = page.frame.origin.y - 10.0;
    [self.scrollView setContentOffset:CGPointMake(0, offset) animated:YES];
}

#pragma mark - Toggle Drawing Mode

- (void)toggleDrawingMode {
    self.isDrawingMode = !self.isDrawingMode;

    for (UIView *page in self.pages) {
        UITextView *textView = page.subviews[0];
        SketchView *drawingView = page.subviews[1];

        textView.hidden = self.isDrawingMode;
        drawingView.hidden = !self.isDrawingMode;
    }
}

#pragma mark - Save Functionality

- (void)saveAndReturn {
    if (self.pages.count == 0) {
        [self dismissViewControllerAnimated:YES completion:nil];
        return;
    }

    NSString *notesDir = [self notesDirectory];
    NSMutableArray *pageDataArray = [NSMutableArray array];

    for (UIView *page in self.pages) {
        UITextView *textView = page.subviews[0];
        SketchView *drawingView = page.subviews[1];

        NSDictionary *pageData = @{
            @"text": textView.text ?: @"",
            @"sketch": [drawingView getDrawingPaths] ?: @[]
        };
        [pageDataArray addObject:pageData];
    }

    NSDictionary *noteData = @{@"pages": pageDataArray};
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:noteData options:NSJSONWritingPrettyPrinted error:nil];
    NSString *jsonPath = [notesDir stringByAppendingPathComponent:@"note.json"];
    BOOL jsonSaved = [jsonData writeToFile:jsonPath atomically:YES];

    if (!jsonSaved) {
        NSLog(@"Error saving JSON file.");
    }

    [self dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - Utility

- (NSString *)notesDirectory {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths firstObject];
    NSString *notesDir = [documentsDirectory stringByAppendingPathComponent:@"saved_notes"];

    if (![[NSFileManager defaultManager] fileExistsAtPath:notesDir]) {
        [[NSFileManager defaultManager] createDirectoryAtPath:notesDir withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return notesDir;
}

@end



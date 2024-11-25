#import "NoteEditorViewController.h"

@interface NoteEditorViewController ()

// UI Elements
@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) UIView *pagesContainer;
@property (nonatomic, strong) UIView *bottomToolbar;

@property (nonatomic) CGSize pageSize;
@property (nonatomic, strong) NSMutableArray<UIView *> *pages; // Pages in the editor
@property (nonatomic, weak) UIView *activePage; // Current active page

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

    UIView *page = [[UIView alloc] initWithFrame:CGRectMake(20, self.pages.count * (pageHeight + 20), pageWidth, pageHeight)];
    page.backgroundColor = [UIColor whiteColor];
    page.layer.borderColor = [UIColor lightGrayColor].CGColor;
    page.layer.borderWidth = 1.0;
    page.layer.cornerRadius = 10.0;

    [self.pagesContainer addSubview:page];
    [self.pages addObject:page];
    self.activePage = page;

    // Update scroll content size
    self.pagesContainer.frame = CGRectMake(0, 0, self.scrollView.bounds.size.width, CGRectGetMaxY(page.frame) + 20);
    self.scrollView.contentSize = self.pagesContainer.frame.size;
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
        NSDictionary *pageData = @{
            @"text": @"Placeholder text",
            @"sketch": @[] // Replace with actual sketch data
        };
        [pageDataArray addObject:pageData];
    }

    NSDictionary *noteData = @{@"pages": pageDataArray};
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:noteData options:NSJSONWritingPrettyPrinted error:nil];

    // Save the JSON data
    NSString *jsonPath = [notesDir stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.json", noteFileName]];
    [jsonData writeToFile:jsonPath atomically:YES];

    // Example placeholder for bitmap saving
    NSString *bitmapPath = [notesDir stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.png", noteFileName]];
    UIGraphicsBeginImageContext(self.activePage.bounds.size);
    [self.activePage.layer renderInContext:UIGraphicsGetCurrentContext()];
    UIImage *bitmap = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    [UIImagePNGRepresentation(bitmap) writeToFile:bitmapPath atomically:YES];

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

@end

#import "NotesListViewController.h"
#import "NoteEditorViewController.h" // Placeholder for the note editor screen

@interface NotesListViewController ()

@property (nonatomic, strong) UIScrollView *notesScrollView; // Scroll view for notes
@property (nonatomic, strong) NSMutableArray<NSString *> *savedNotes; // List of saved notes
@property (nonatomic, strong) NSString *notesDirectory; // Path to the notes directory

@end

@implementation NotesListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Set up the view
    self.view.backgroundColor = [UIColor whiteColor];
    self.notesDirectory = [self getNotesDirectory];
    self.savedNotes = [NSMutableArray array];

    // Create a "Create Note" button
    UIButton *createNoteButton = [UIButton buttonWithType:UIButtonTypeSystem];
    [createNoteButton setTitle:@"Create Note" forState:UIControlStateNormal];
    createNoteButton.frame = CGRectMake(20, 40, self.view.frame.size.width - 40, 50);
    createNoteButton.backgroundColor = [UIColor colorWithRed:0.85 green:0.07 blue:0.43 alpha:1.0];
    [createNoteButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    createNoteButton.layer.cornerRadius = 10;
    [createNoteButton addTarget:self action:@selector(openNoteCreator) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:createNoteButton];

    // Set up the scroll view for notes
    self.notesScrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 100, self.view.frame.size.width, self.view.frame.size.height - 100)];
    [self.view addSubview:self.notesScrollView];

    // Load saved notes
    [self loadSavedNotes];
}

// Get the path to the notes directory
- (NSString *)getNotesDirectory {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths firstObject];
    NSString *notesDirectory = [documentsDirectory stringByAppendingPathComponent:@"saved_notes"];
    
    if (![[NSFileManager defaultManager] fileExistsAtPath:notesDirectory]) {
        [[NSFileManager defaultManager] createDirectoryAtPath:notesDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return notesDirectory;
}

// Load all saved notes and display them
- (void)loadSavedNotes {
    [self.savedNotes removeAllObjects];
    [self.notesScrollView.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSArray *files = [fileManager contentsOfDirectoryAtPath:self.notesDirectory error:nil];
    
    CGFloat x = 20;
    CGFloat y = 20;
    CGFloat noteSize = 100;

    for (NSString *fileName in files) {
        if ([fileName hasSuffix:@".png"]) {
            NSString *notePath = [self.notesDirectory stringByAppendingPathComponent:fileName];
            NSString *jsonPath = [[notePath stringByDeletingPathExtension] stringByAppendingPathExtension:@"json"];
            
            if ([fileManager fileExistsAtPath:jsonPath]) {
                [self.savedNotes addObject:fileName];
                [self addNoteToScrollView:fileName atPosition:CGPointMake(x, y)];
                x += noteSize + 20;
                
                if (x + noteSize > self.view.frame.size.width) {
                    x = 20;
                    y += noteSize + 20;
                }
            }
        }
    }
    self.notesScrollView.contentSize = CGSizeMake(self.view.frame.size.width, y + noteSize + 20);
}

// Add a note to the scroll view
- (void)addNoteToScrollView:(NSString *)noteFileName atPosition:(CGPoint)position {
    NSString *notePath = [self.notesDirectory stringByAppendingPathComponent:noteFileName];
    UIImage *noteImage = [UIImage imageWithContentsOfFile:notePath];

    if (noteImage) {
        UIButton *noteButton = [UIButton buttonWithType:UIButtonTypeCustom];
        noteButton.frame = CGRectMake(position.x, position.y, 100, 100);
        [noteButton setImage:noteImage forState:UIControlStateNormal];
        noteButton.layer.cornerRadius = 10;
        noteButton.clipsToBounds = YES;
        noteButton.backgroundColor = [UIColor lightGrayColor];
        noteButton.tag = [self.savedNotes indexOfObject:noteFileName];
        [noteButton addTarget:self action:@selector(openNoteEditor:) forControlEvents:UIControlEventTouchUpInside];
        [self.notesScrollView addSubview:noteButton];
    }
}

// Open the note editor for a specific note
- (void)openNoteEditor:(UIButton *)sender {
    NSString *noteFileName = self.savedNotes[sender.tag];
    NoteEditorViewController *noteEditor = [[NoteEditorViewController alloc] init];
    noteEditor.noteFileName = noteFileName;
    [self presentViewController:noteEditor animated:YES completion:nil];
}

// Open the note creator
- (void)openNoteCreator {
    NoteEditorViewController *noteEditor = [[NoteEditorViewController alloc] init];
    [self presentViewController:noteEditor animated:YES completion:^{
        // Optionally reload after note creation
        [self loadSavedNotes];
    }];
}

@end

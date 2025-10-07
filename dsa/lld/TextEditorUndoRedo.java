package lld;

import java.util.*;

/**
 * LLD #82: Text Editor Undo/Redo with Command + Memento Pattern
 * 
 * Design Patterns:
 * 1. Command Pattern - Encapsulate edit operations
 * 2. Memento Pattern - Store document state snapshots
 * 3. Composite Pattern - Complex commands from simple ones
 * 
 * Supports: Insert, Delete, Replace with full undo/redo
 */

// Memento Pattern - Stores state snapshot
class EditorMemento {
    private final String content;
    private final int cursorPosition;
    private final long timestamp;
    
    public EditorMemento(String content, int cursorPosition) {
        this.content = content;
        this.cursorPosition = cursorPosition;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getContent() { return content; }
    public int getCursorPosition() { return cursorPosition; }
    public long getTimestamp() { return timestamp; }
}

// Command Pattern - Edit operations
interface EditorCommand {
    void execute();
    void undo();
    String getDescription();
}

class InsertCommand implements EditorCommand {
    private TextEditor editor;
    private String text;
    private int position;
    private EditorMemento beforeState;
    
    public InsertCommand(TextEditor editor, String text, int position) {
        this.editor = editor;
        this.text = text;
        this.position = position;
    }
    
    @Override
    public void execute() {
        beforeState = editor.createMemento();
        editor.insertAtPosition(text, position);
    }
    
    @Override
    public void undo() {
        if (beforeState != null) {
            editor.restoreFromMemento(beforeState);
        }
    }
    
    @Override
    public String getDescription() {
        return "Insert '" + text + "' at " + position;
    }
}

class DeleteCommand implements EditorCommand {
    private TextEditor editor;
    private int start;
    private int end;
    private EditorMemento beforeState;
    
    public DeleteCommand(TextEditor editor, int start, int end) {
        this.editor = editor;
        this.start = start;
        this.end = end;
    }
    
    @Override
    public void execute() {
        beforeState = editor.createMemento();
        editor.deleteRange(start, end);
    }
    
    @Override
    public void undo() {
        if (beforeState != null) {
            editor.restoreFromMemento(beforeState);
        }
    }
    
    @Override
    public String getDescription() {
        return "Delete range [" + start + ", " + end + "]";
    }
}

class ReplaceCommand implements EditorCommand {
    private TextEditor editor;
    private int start;
    private int end;
    private String newText;
    private EditorMemento beforeState;
    
    public ReplaceCommand(TextEditor editor, int start, int end, String newText) {
        this.editor = editor;
        this.start = start;
        this.end = end;
        this.newText = newText;
    }
    
    @Override
    public void execute() {
        beforeState = editor.createMemento();
        editor.replaceRange(start, end, newText);
    }
    
    @Override
    public void undo() {
        if (beforeState != null) {
            editor.restoreFromMemento(beforeState);
        }
    }
    
    @Override
    public String getDescription() {
        return "Replace [" + start + ", " + end + "] with '" + newText + "'";
    }
}

// Composite Pattern - Macro command
class MacroCommand implements EditorCommand {
    private List<EditorCommand> commands;
    private String description;
    
    public MacroCommand(String description) {
        this.commands = new ArrayList<>();
        this.description = description;
    }
    
    public void addCommand(EditorCommand command) {
        commands.add(command);
    }
    
    @Override
    public void execute() {
        for (EditorCommand cmd : commands) {
            cmd.execute();
        }
    }
    
    @Override
    public void undo() {
        // Undo in reverse order
        for (int i = commands.size() - 1; i >= 0; i--) {
            commands.get(i).undo();
        }
    }
    
    @Override
    public String getDescription() {
        return description + " (" + commands.size() + " operations)";
    }
}

class TextEditor {
    private StringBuilder content;
    private int cursorPosition;
    private Stack<EditorCommand> undoStack;
    private Stack<EditorCommand> redoStack;
    private static final int MAX_UNDO_SIZE = 100;
    
    public TextEditor() {
        this.content = new StringBuilder();
        this.cursorPosition = 0;
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }
    
    // MAIN ALGORITHM: Execute command and manage undo/redo stacks
    public void executeCommand(EditorCommand command) {
        command.execute();
        
        // Add to undo stack
        undoStack.push(command);
        
        // Limit undo stack size
        if (undoStack.size() > MAX_UNDO_SIZE) {
            undoStack.remove(0);
        }
        
        // Clear redo stack on new command
        redoStack.clear();
    }
    
    public boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }
        
        EditorCommand command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        return true;
    }
    
    public boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }
        
        EditorCommand command = redoStack.pop();
        command.execute();
        undoStack.push(command);
        return true;
    }
    
    // Core editor operations (called by commands)
    public void insertAtPosition(String text, int pos) {
        if (pos < 0 || pos > content.length()) {
            throw new IllegalArgumentException("Invalid position");
        }
        content.insert(pos, text);
        cursorPosition = pos + text.length();
    }
    
    public void deleteRange(int start, int end) {
        if (start < 0 || end > content.length() || start > end) {
            throw new IllegalArgumentException("Invalid range");
        }
        content.delete(start, end);
        cursorPosition = start;
    }
    
    public void replaceRange(int start, int end, String newText) {
        deleteRange(start, end);
        insertAtPosition(newText, start);
    }
    
    // Memento methods
    public EditorMemento createMemento() {
        return new EditorMemento(content.toString(), cursorPosition);
    }
    
    public void restoreFromMemento(EditorMemento memento) {
        content = new StringBuilder(memento.getContent());
        cursorPosition = memento.getCursorPosition();
    }
    
    // Utility methods
    public String getContent() {
        return content.toString();
    }
    
    public int getCursorPosition() {
        return cursorPosition;
    }
    
    public void setCursorPosition(int position) {
        if (position >= 0 && position <= content.length()) {
            cursorPosition = position;
        }
    }
    
    public int getUndoStackSize() {
        return undoStack.size();
    }
    
    public int getRedoStackSize() {
        return redoStack.size();
    }
    
    public void clear() {
        content.setLength(0);
        cursorPosition = 0;
        undoStack.clear();
        redoStack.clear();
    }
}

public class TextEditorUndoRedo {
    
    public static void main(String[] args) {
        TextEditor editor = new TextEditor();
        
        System.out.println("=== Text Editor with Undo/Redo ===\n");
        
        // Insert "Hello"
        EditorCommand cmd1 = new InsertCommand(editor, "Hello", 0);
        editor.executeCommand(cmd1);
        System.out.println("After insert 'Hello': " + editor.getContent());
        
        // Insert " World"
        EditorCommand cmd2 = new InsertCommand(editor, " World", 5);
        editor.executeCommand(cmd2);
        System.out.println("After insert ' World': " + editor.getContent());
        
        // Delete "World"
        EditorCommand cmd3 = new DeleteCommand(editor, 6, 11);
        editor.executeCommand(cmd3);
        System.out.println("After delete 'World': " + editor.getContent());
        
        // Undo delete
        System.out.println("\nUndoing...");
        editor.undo();
        System.out.println("After undo: " + editor.getContent());
        
        // Undo insert
        System.out.println("\nUndoing...");
        editor.undo();
        System.out.println("After undo: " + editor.getContent());
        
        // Redo
        System.out.println("\nRedoing...");
        editor.redo();
        System.out.println("After redo: " + editor.getContent());
        
        // Test macro command
        System.out.println("\n=== Testing Macro Command ===");
        editor.clear();
        
        MacroCommand macro = new MacroCommand("Format text");
        macro.addCommand(new InsertCommand(editor, "Java", 0));
        macro.addCommand(new InsertCommand(editor, " Programming", 4));
        macro.addCommand(new ReplaceCommand(editor, 0, 4, "Python"));
        
        editor.executeCommand(macro);
        System.out.println("After macro: " + editor.getContent());
        
        editor.undo();
        System.out.println("After undo macro: " + editor.getContent());
        
        System.out.println("\nUndo stack size: " + editor.getUndoStackSize());
        System.out.println("Redo stack size: " + editor.getRedoStackSize());
    }
}

/*
 * INTERVIEW QUESTIONS & ANSWERS:
 * 
 * Q1: Why use both Command and Memento patterns?
 * A: Command encapsulates operations (what was done).
 *    Memento captures state (what it was before).
 *    Together: Command for undo logic, Memento for state restoration.
 *    Trade-off: Memento uses more memory but simpler undo logic.
 * 
 * Q2: How to optimize memory for large documents?
 * A: Instead of storing full content in Memento, store only:
 *    - Delta changes (what was inserted/deleted)
 *    - Use diff algorithms (Myers diff)
 *    - Compress old mementos
 *    - Limit undo stack size
 *    - Use rope data structure for efficient edits
 * 
 * Q3: How would you implement undo/redo groups (transactions)?
 * A: Use Composite/Macro command pattern. Group related operations:
 *    - Start transaction → create MacroCommand
 *    - Add commands to macro
 *    - End transaction → execute macro
 *    - Undo: reverses entire group atomically
 * 
 * Q4: How to handle concurrent edits in collaborative editing?
 * A: Operational Transformation (OT) or CRDTs:
 *    - Transform operations based on concurrent edits
 *    - Resolve conflicts automatically
 *    - Each user has local undo/redo stack
 *    - Broadcast operations to other users
 * 
 * Q5: What's the time/space complexity?
 * A: Time: O(1) for undo/redo (stack operations)
 *         O(N) for command execution where N = text length
 *    Space: O(M * N) where M = undo stack size, N = content size
 *    Optimization: Use delta storage → O(M * D) where D = delta size
 * 
 * Q6: How would you implement selective undo (undo specific operation)?
 * A: More complex! Need to:
 *    - Identify target operation in history
 *    - Reverse it while preserving later operations
 *    - Use transformation to adjust positions
 *    - Or rebuild state by replaying non-undone operations
 * 
 * Q7: How to implement undo/redo for complex objects (images, tables)?
 * A: Use Memento for entire object state, or:
 *    - Custom command for each edit type
 *    - Store object-specific diffs
 *    - Use serialization/cloning for state capture
 *    - Separate undo stack per object type
 * 
 * Q8: How would you persist undo/redo across sessions?
 * A: Serialize command history to file/database:
 *    - Store command type and parameters
 *    - Reconstruct commands on load
 *    - Replay commands to restore state
 *    - Consider size limits (don't persist all history)
 * 
 * Q9: How to implement time-travel debugging with undo/redo?
 * A: Enhanced memento with timestamps:
 *    - Store timestamp with each memento
 *    - Allow jumping to specific time point
 *    - Visualize state at any point in history
 *    - Useful for debugging, version comparison
 * 
 * Q10: How would you optimize for mobile devices (limited memory)?
 * A: Aggressive optimizations:
 *    - Smaller undo stack (10-20 operations)
 *    - Compress mementos
 *    - Use delta-based approach
 *    - Discard old history automatically
 *    - Lazy memento creation (only on significant edits)
 */

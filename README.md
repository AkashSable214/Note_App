# Note_App

## Overview
This is a **feature-rich text-based Notes Manager** built in Java using **File I/O**.  
It allows you to create, search, delete, backup, and restore notes.  
Notes are stored in a text file (`notes.txt`) and include a timestamp.

## Features
- **Add New Note**: Create a note with title, content, and timestamp.
- **View All Notes**: Display all stored notes.
- **Search Notes**: Search notes by keyword in title or content.
- **Delete Note**: Remove a note by its title.
- **Backup Notes**: Create a backup copy of all notes in `notes_backup.txt`.
- **Restore Notes**: Restore notes from the backup file.
- **Persistent Storage**: Notes are saved to disk and remain after the program closes.

## Technologies Used
- Java 8+
- File I/O: `FileWriter`, `BufferedWriter`, `FileReader`, `BufferedReader`
- Exception Handling
- `LocalDateTime` & `DateTimeFormatter` for timestamps
- Collections API (`ArrayList`)

## How It Works
1. **Adding a Note**  
   - User enters a title and content.
   - Content entry ends when the user types `END`.
   - The note is appended to `notes.txt` with a timestamp.

2. **Searching Notes**  
   - User provides a keyword.
   - The program searches through all notes and displays matches.

3. **Deleting Notes**  
   - The note with the given title is removed from the file.

4. **Backup & Restore**  
   - Backup: Copies all notes to `notes_backup.txt`.
   - Restore: Overwrites `notes.txt` with the backup contents.

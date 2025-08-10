package com.notApp;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotesApp {
    
    private static final String NOTES_FILE = "notes.txt";
    private static final String BACKUP_FILE = "notes_backup.txt";
    private static final Scanner sc = new Scanner(System.in);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
      
    /**
     * Display the main menu options
     */
    private static void displayMenu() {
        System.out.println("\n=== Notes App Menu ===");
        System.out.println("1. Add New Note");
        System.out.println("2. View All Notes");
        System.out.println("3. Search Notes");
        System.out.println("4. Delete Note");
        System.out.println("5. Backup Notes");
        System.out.println("6. Restore from Backup");
        System.out.println("7. Exit");
        System.out.print("Enter your choice (1-7): ");
    }
    
    
    private static int getValidChoice() {
        while (true) {
            try {
                int choice = Integer.parseInt(sc.nextLine());
                if (choice >= 1 && choice <= 7) {
                    return choice;
                } else {
                    System.out.print("Please enter a number between 1 and 7: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
    
    
    // Add a new note to the file
    
    private static void addNote() {
        System.out.println("\n=== Add New Note ===");
        System.out.print("Enter note title: ");
        String title = sc.nextLine().trim();
        
        if (title.isEmpty()) {
            System.out.println("Title cannot be empty!");
            return;
        }
        
        System.out.println("Enter note content (type 'END' on a new line to finish):");
        StringBuilder content = new StringBuilder();
        String line;
        
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            if (line.equals("END")) {
                break;
            }
            content.append(line).append("\n");
        }
        
        if (content.toString().trim().isEmpty()) {
            System.out.println("Note content cannot be empty!");
            return;
        }
        
        // Create note entry with timestamp
        String timestamp = LocalDateTime.now().format(formatter);
        String noteEntry = String.format("=== %s ===\nDate: %s\n%s\n", 
                                       title, timestamp, content.toString().trim());
        
        // Write to file using FileWriter
        try (FileWriter fileWriter = new FileWriter(NOTES_FILE, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            
            bufferedWriter.write(noteEntry);
            bufferedWriter.newLine();
            bufferedWriter.write("=".repeat(50));
            bufferedWriter.newLine();
            
            System.out.println("Note added successfully!");
            
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
    
    
     // View all notes from the file
     
    private static void viewAllNotes() {
        System.out.println("\n=== All Notes ===");
        
        if (!fileExists(NOTES_FILE)) {
            System.out.println("No notes found. Add some notes first!");
            return;
        }
        
        try (FileReader fileReader = new FileReader(NOTES_FILE);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            
            String line;
            boolean hasNotes = false;
            
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                hasNotes = true;
            }
            
            if (!hasNotes) {
                System.out.println("No notes found in the file.");
            }
            
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }
    }
    
   
     // Search notes by keyword
     
    private static void searchNotes() {
        System.out.println("\n=== Search Notes ===");
        System.out.print("Enter search keyword: ");
        String keyword = sc.nextLine().toLowerCase().trim();
        
        if (keyword.isEmpty()) {
            System.out.println("Keyword cannot be empty!");
            return;
        }
        
        if (!fileExists(NOTES_FILE)) {
            System.out.println("No notes found. Add some notes first!");
            return;
        }
        
        try (FileReader fileReader = new FileReader(NOTES_FILE);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            
            String line;
            boolean found = false;
            boolean inNote = false;
            StringBuilder currentNote = new StringBuilder();
            
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("=== ") && line.endsWith(" ===")) {
                    // Start of a new note
                    if (inNote && currentNote.toString().toLowerCase().contains(keyword)) {
                        System.out.println(currentNote.toString());
                        System.out.println("=".repeat(50));
                        found = true;
                    }
                    currentNote = new StringBuilder(line + "\n");
                    inNote = true;
                } else if (line.equals("=".repeat(50))) {
                    // End of note
                    if (inNote && currentNote.toString().toLowerCase().contains(keyword)) {
                        System.out.println(currentNote.toString());
                        System.out.println(line);
                        found = true;
                    }
                    inNote = false;
                    currentNote = new StringBuilder();
                } else if (inNote) {
                    currentNote.append(line).append("\n");
                }
            }
            
            // Check the last note
            if (inNote && currentNote.toString().toLowerCase().contains(keyword)) {
                System.out.println(currentNote.toString());
                found = true;
            }
            
            if (!found) {
                System.out.println("No notes found containing the keyword: " + keyword);
            }
            
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }
    }
    
    
    // Delete a note by title
    
    private static void deleteNote() {
        System.out.println("\n=== Delete Note ===");
        System.out.print("Enter the title of the note to delete: ");
        String titleToDelete = sc.nextLine().trim();
        
        if (titleToDelete.isEmpty()) {
            System.out.println("Title cannot be empty!");
            return;
        }
        
        if (!fileExists(NOTES_FILE)) {
            System.out.println("No notes found. Add some notes first!");
            return;
        }
        
        List<String> lines = new ArrayList<>();
        boolean found = false;
        boolean skipNote = false;
        
        try (FileReader fileReader = new FileReader(NOTES_FILE);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("=== ") && line.endsWith(" ===")) {
                    String noteTitle = line.substring(4, line.length() - 4).trim();
                    if (noteTitle.equals(titleToDelete)) {
                        skipNote = true;
                        found = true;
                        continue;
                    } else {
                        skipNote = false;
                    }
                }
                
                if (!skipNote) {
                    lines.add(line);
                }
            }
            
            // Write back to file
            try (FileWriter fileWriter = new FileWriter(NOTES_FILE);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                
                for (String l : lines) {
                    bufferedWriter.write(l);
                    bufferedWriter.newLine();
                }
                
                if (found) {
                    System.out.println("Note deleted successfully!");
                } else {
                    System.out.println("Note with title '" + titleToDelete + "' not found.");
                }
                
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }
            
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }
    }
    
    
    // Create a backup of the notes file
    
    private static void backupNotes() {
        System.out.println("\n=== Backup Notes ===");
        
        if (!fileExists(NOTES_FILE)) {
            System.out.println("No notes file to backup!");
            return;
        }
        
        try (FileReader fileReader = new FileReader(NOTES_FILE);
             BufferedReader bufferedReader = new BufferedReader(fileReader);
             FileWriter fileWriter = new FileWriter(BACKUP_FILE);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            
            System.out.println("Backup created successfully as: " + BACKUP_FILE);
            
        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
        }
    }
    
    
    // Restore notes from backup file
    private static void restoreFromBackup() {
        System.out.println("\n=== Restore from Backup ===");
        
        if (!fileExists(BACKUP_FILE)) {
            System.out.println("No backup file found!");
            return;
        }
        
        try (FileReader fileReader = new FileReader(BACKUP_FILE);
             BufferedReader bufferedReader = new BufferedReader(fileReader);
             FileWriter fileWriter = new FileWriter(NOTES_FILE);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            
            System.out.println("Notes restored successfully from backup!");
            
        } catch (IOException e) {
            System.err.println("Error restoring from backup: " + e.getMessage());
        }
    }
    
    
    // Check if a file exists
    
    private static boolean fileExists(String fileName) {
        File file = new File(fileName);
        return file.exists() && file.length() > 0;
    }
    
    
    public static void main(String[] args) {
        System.out.println("=== Welcome to Notes App ===");
        System.out.println();
        
        while (true) {
            displayMenu();
            int choice = getValidChoice();
            
            switch (choice) {
                case 1:
                    addNote();
                    break;
                case 2:
                    viewAllNotes();
                    break;
                case 3:
                    searchNotes();
                    break;
                case 4:
                    deleteNote();
                    break;
                case 5:
                    backupNotes();
                    break;
                case 6:
                    restoreFromBackup();
                    break;
                case 7:
                    System.out.println("Thank you for using Notes App!");
                    sc.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            
            System.out.println("\nPress Enter to continue...");
            sc.nextLine();
        }
    }
} 


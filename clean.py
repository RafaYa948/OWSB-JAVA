import os
import re
import sys

def remove_single_line_comments(file_path):
    """Remove single-line comments (//) from a Java file."""
    try:
        # Read the file
        with open(file_path, 'r', encoding='utf-8') as file:
            content = file.read()
        
        # Remove single-line comments
        # This regex matches // and everything after it until the end of the line
        # but preserves the newline character
        cleaned_content = re.sub(r'//.*?(?=\n|$)', '', content)
        
        # Write the cleaned content back to the file
        with open(file_path, 'w', encoding='utf-8') as file:
            file.write(cleaned_content)
            
        return True
    except Exception as e:
        print(f"Error processing {file_path}: {e}")
        return False

def process_java_files(root_dir):
    """Process all Java files in the given directory and its subdirectories."""
    processed_files = 0
    failed_files = 0
    
    for dirpath, dirnames, filenames in os.walk(root_dir):
        for filename in filenames:
            if filename.endswith('.java'):
                file_path = os.path.join(dirpath, filename)
                success = remove_single_line_comments(file_path)
                
                if success:
                    processed_files += 1
                    print(f"Processed: {file_path}")
                else:
                    failed_files += 1
    
    return processed_files, failed_files

def main():
    # Get the root directory (where the project is located)
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.dirname(script_dir)  # Go up one level from the script location
    
    # Find all src directories
    src_dirs = []
    for dirpath, dirnames, _ in os.walk(project_root):
        if 'src' in dirnames:
            src_dirs.append(os.path.join(dirpath, 'src'))
    
    if not src_dirs:
        print("No 'src' directories found in the project.")
        return
    
    total_processed = 0
    total_failed = 0
    
    print(f"Found {len(src_dirs)} 'src' directories to process.")
    for src_dir in src_dirs:
        print(f"\nProcessing Java files in {src_dir}...")
        processed, failed = process_java_files(src_dir)
        total_processed += processed
        total_failed += failed
    
    print(f"\nFinal Summary:")
    print(f"- Successfully processed {total_processed} Java files")
    if total_failed > 0:
        print(f"- Failed to process {total_failed} Java files")
    print("Done!")

if __name__ == "__main__":
    main()
# PowerPurge
A Java file cleanup utility. Allows defining file purge/archive rules per directory and executes them. Can be used for cleaning up old data files, log files, etc.

The program runs based on a specified config file that contains the purge rules and associated paths.

### Config File
The config file is a .json file with the following structure:

    [
        {
            "name": "My purge rule",
            "filePattern": "*.*",
            "recursive": "true",
            "fileAgeDays": 30,
            "archiveFolder": "archive",
            "archiveAgeDays": 90,
            "paths": [
                "/path1/path2",
                "/path3/path4"
            ]
        },
        {
            "name": "Another purge rule",
            ...
        }
    ]

Rule Properties:
- name: The name of the rule. It can be anything and doesn't have to be unique.
- filePattern: a list of file/wildcard patterns to be purged. Default value is "*" (all files). Multiple patterns can be specified by separating the list with semicolons. For example: "*.txt;*.log;*.xml"
- recursive: a flag indicating whether to apply these rules to child folders (default=false)
- fileAgeDays: files older than this number of days will be purged (default=-1, meaning do not purge)
- archiveFolder: specifies the name of the subfolder to store archive .zip files in. Default=none (do not archive)
- archiveAgeDays: archive .zip files older than this number of days will be removed from the archive folder.


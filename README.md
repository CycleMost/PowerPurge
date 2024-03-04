# PowerPurge
A Java file cleanup utility. Allows defining file purge/archive rules per directory and executes them. Can be used for cleaning up old data files, log files, etc.

The program runs based on a specified config file that contains the purge rules and associated paths.

### Config File
The config file is a .json file with the following structure:

```json
[
    {
        "name": "My purge rule",
        "filePattern": "*.*",
        "recursive": true,
        "fileAgeDays": 30,
        "archiveAgeDays": 90,
        "paths": [
            "/path1/sub1",
            "/path2/sub2"
        ]
    },
    {
        "name": "Another purge rule",
        ...
    }
]
```

### Rule Properties:
- <code>name</code>: The name of the rule. It can be anything and doesn't have to be unique.
- <code>filePattern</code>: a list of file/wildcard patterns to be purged. Default value is "\*" (all files). Multiple patterns can be specified by separating the list with semicolons. For example: "\*.txt;\*.log;\*.xml"
- <code>recursive</code>: a flag indicating whether to apply these rules to all child folders (default=false)
- <code>fileAgeDays</code>: files older than this number of days will be purged (default=-1, meaning do not purge)
- <code>archiveAgeDays</code>: if greater than zero, deleted files will be placed into an archive zip file in the folder they were deleted from. The archive filename will be "archive-{date}_{time}.zip". These archive files will be removed by PowerPurge once they are <code>archiveAgeDays</code> old. If zero (the default), archives will not be created.


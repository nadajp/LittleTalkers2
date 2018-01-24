# LittleTalkers2
New and improved Little Talkers - exported to Android Studio, refactored to include ContentProvider, Loaders, improved inheritance, error checking.

Little Talkers is an app that helps parents and caretakers record their childrens' first words, phrases and questions/answers.  It allows users to write out the phrases as well as make audio recordings, then displays them alphabetically or by date. The data is stored in the local database and synced to cloud using the Sync Adapter.

The source code is divided into the following sections:
- contentprovider
- database 
- model (the data model)
- sync (implements syncadapter to backup data to cloud)
- ui (where all the activities are located)
- utils (miscellaneous utility functions)
- widget (latest version includes a widget for easy access to the recording page from home screen)

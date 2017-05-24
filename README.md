# CaveSaveEdit
A Java-based Cave Story profile editor, complete with mod support.
# How to Use
Click on "Load profile" in the toolbar to load a profile. If a Profile.dat file is found in the same directory as the application, it will be loaded automatically on startup.  
Modify the values to your liking, and then click on "Save" in the toolbar to save the new profile.  
Yes, it's that simple!
# MCI
The MCI system is used for mod support.  
This system allows users to add custom items, weapons, equipment and more to the save editor with ease.  
See [the MCI readme](MCI.md) for more information about the MCI system.
# Libraries
This application only uses a single library, namely [juniversalchardet](https://code.google.com/archive/p/juniversalchardet/). If you want to compile from source, be sure to download version 1.0.3 and put it in the same folder as the .classpath file.
# Credits
- **Noxid ([@taedixon](https://github.com/taedixon)):** CS executable code ([frontend.data](src/com/leo/cse/frontend/data))
- **zxin ([@zxinmine](https://github.com/zxinmine)):** UI sprites ([ui.png](src/com/leo/cse/frontend/ui.png))
- **Carrotlord:** StrTools class ([StrTools.java](src/com/carrotlord/string/StrTools.java))

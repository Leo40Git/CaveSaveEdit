# CaveSaveEdit [![Build Status](https://travis-ci.org/Leo40Git/CaveSaveEdit.svg?branch=master)](https://travis-ci.org/Leo40Git/CaveSaveEdit)
A Java-based Cave Story profile editor, complete with mod support.
# How to Compile
First off, CaveSaveEdit will only compile on Java 8 without modifications to the map sorting functions in `com.leo.cse.frontend.FrontUtils` and a few other places where lambdas are used.  
CSE has a dependency on [Rhino](https://github.com/mozilla/rhino). Versions 1_7R5, 1.7.7.2 and 1.7.8 have been confirmed to work, although other versions might work as well.  
There is only one `main` method, located in `com.leo.cse.frontend.Main`.  
This is an Eclipse project, although I believe IntelliJ can import Eclipse projects natively.  
# How to Use
Click on "File" -> "Load Profile" to load a profile. If a Profile.dat file is found in the same directory as the application, it will be loaded automatically on startup.  
Modify the values to your liking, and then click on "File" -> "Save" in the toolbar to save the new profile.  
Yes, it's that simple!
# MCI
The MCI system is used for mod support.  
This system allows users to add custom items, weapons, equipment and more to the save editor with ease.  
See [the MCI readme](MCI.md) for more information about the MCI system.  
Want to share MCI files you have created or obtain MCI files that other people have made? Check the **[MCI Repository](https://github.com/Leo40Git/CSE-MCI-Repository)**!
# Notes
This editor only works with vanilla profiles, meaning that any mod with custom profiles (i.e. any of txin's mods excluding The Ultimate Challenge) will **not** work.  
You _can_ edit save files with modified extensions, just change the file filter on the "open profile" window to "All files" and you're good.
# Credits
- **Noxid ([@taedixon](https://github.com/taedixon), [@Noxid](https://www.cavestory.org/forums/members/noxid.863)):** Executable related code (classes in `com.leo.cse.backend.exe`), taken with~~out~~ permission from **[Booster's Lab](https://github.com/taedixon/boosters-lab)**.
- **20kdc ([@20kdc](https://github.com/20kdc), [@gamemanj](https://www.cavestory.org/forums/members/gamemanj.7022)):** ".rsrc" segment-related code in ExeData
- **zxin ([@zxinmine](https://github.com/zxinmine), [@zxin](https://www.cavestory.org/forums/members/zxin.7232)):** UI sprites (`ui.png` and `shadow.png`) 
- **Carrotlord ([@Carrotlord](https://www.cavestory.org/forums/members/carrotlord.1111)):** StrTools class (`com.leo.cse.backend.StrTools`)

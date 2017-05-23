# What is MCI?
The MCI system, or the **M**od **C**ompatibility **I**nformation system (originally just called the "defines" system) is the system this save editor uses for mod compatibility. It's exactly what it says on the tin.

# How does MCI work?
An MCI file is simply a Java properties file with a different file extension.  
There are several groups of strings which are divided by their entry's prefix.  
Each group serves a purpose in the save editor UI:
- `Map` - Map names
- `Song` - Song names
- `Equip` - Equipment names
- `Weapon` - Weapon names
- `Item` - Item names
- `Warp` - Warp names
- `WarpLoc` - Warp locations (event IDs)
- `Flag` - Flag descriptions

In most cases, only IDs which have been defined can be used as values in the save editor.  
All of these groups, excluding the `Map` group, should have ID 0 set to "None" or the equivalent
thereof.

# Special entries

Each group has it's entry's prefix shown in it's title.  
These are formatted as `Type:Entry`, with `Type` being the type of the value (either `String` or `Boolean`) and `Entry` being the entry name.

## `Meta` - Metadata:

This group is used for defining identifying information for this MCI file.
- `String:Name`: Name of the mod these defines are associated with.
- `String:Author`: Name(s) of the author(s) of the defines.

## `Game` - Game information:

This group is used for defining information about the mod this MCI file supports.
- `String:Encoding`: The encoding used in the mod.
- `String:ExeName`: The expected name of the mod executable, *without file extension*.
- `String:GraphicsExtension`: The file extension for graphics files. "bmp" and "pbm" are interchangeable.
- `String:MyChar`: The file name for the `MyChar` graphics files, because apparently some mods (*cough*WTF Story*cough*) rename it. *This is optional, and defaults to "MyChar" if not specified.*

## `Special` - Special support:

This group is used for declaring if support for certain ASM hacks should be enabled or not.
- `Boolean:MimHack`: Adds support for the <MIM hack. Prevents the used flags from being edited in the flag UI and adds a new field for modifying the current costume ID. 
- `Boolean:VarHack`: Adds support for the <VAR (TSC+) hack. Prevents the used flags from being edited in the flag UI and adds a new tab for modifying the variables.
- `Boolean:PhysVarHack`: Adds support for the <PHY (TSC+ PHY addon) hack. Prevents the used flags from being edited in the flag UI and adds new fields for modifying the physics variables.
- `Boolean:DoubleRes`: Adds support for the 2x resolution hack. Makes all graphics use the correct framerects.

## `Flag` - Flag descriptions:

While most entries in this group are used to define a description for a specific flag ID, these entries are used to declare descriptions for certain flag groups.
- `String:Engine`: Description for engine flags, which include all flags below 10.
- `String:MimHack`: Description for flags used by <MIM, which are flags 7968-7993.
- `String:VarHack`: Description for flags used by <VAR, which are flags 6000-8000.
- `String:PhysVarHack`: Description for flags used by <PHY, which are flags 5632-5888.
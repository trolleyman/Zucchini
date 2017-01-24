# c3 (Zucchini)

## Setup

First navigate into your Eclipse Workspace folder from the command line. 
Then type `git clone https://git-teaching.cs.bham.ac.uk/mod-team-proj-2016/c3.git`.

Open up Eclipse and click on `File->New->Java Project...`. Type in `c3` for the project name and press Finish.

Go to `Window->Preferences`, then navigate to `Java->Build Path->User Libraries`. Press `Import...`. Click `Browse...` and navigate to the project location. Then double click on `lwjgl.userlibraries`, and press Ok, and Ok again.

Now we'll import JOML.

Press `File->Import...`, then double click on `Maven->Existing Maven Projects`. Navigate to the project folder if it isn't already there, and click `Finish` to import JOML as a Java Project into Eclipse.

Finally! Everything should be imported and setup correctly.

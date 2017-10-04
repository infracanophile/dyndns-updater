Dyndns Updater

Updates Fenton's dynamic dns name to the ip of whoever runs it last.

To build/run

0. Install boot (https://github.com/boot-clj/boot)
1. run `boot build` from the main directory to compile the js
2. Look in the new `target` directory
3. Copy that main.js to where you want it to be or leave it there
4a. Run `node main.js` with the config.edn file in the same dir
4b. Run `node main.js path/to/config.edn`
5. Change Fenton's server dns ip so he can't find his server

To hack on it

1. run `boot dev` in a terminal and leave it running
2. change something
3. it will incrementally compile every time you touch a file in the project
4. run `node main.js` *from the target directory without moving it*
5. goto 2.
6. Keep changing Fenton's server dns ip so he never finds his server again

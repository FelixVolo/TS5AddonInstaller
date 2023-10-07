# TS5 Addon Installer #
The TS5 Addon Installer is an unofficial tool that simplifies the process of installing and managing addons for TeamSpeak 5.
It featues a builtin patcher to re-enable addon support for the latest versions of TeamSpeak 5.

⚠️ Addons need to be reinstalled after every TeamSpeak update

## Installing Addons ##
The TS5 Addon Installer can automatically download and install addons or install them from zip files.

### Automatic Download ###
1. Select your TS5 installation directory
2. Select the addon you want to install
3. Optional: Select a specific version
   1. Click on "Load"
   2. Select the version you want to install
4. Click on "Install"

### Local File or Folder ###
1. Select your TS5 installation directory
2. Select "Local Addon"
3. Select the addon you want to install
4. Click "Install"

## Uninstalling Addons ##
The TS5 Addon Installer can automatically detect addons you have already installed with the installer and gives you the option to uninstall them again.

1. Select the "Uninstall" tab
2. Select your TS5 installation directory
3. Click on "Load"
4. Select the addon you want to uninstall
5. Click on "Uninstall"

## Command Line Interface ##
The TS5 Addon Installer can be used from the command line with the following usage pattern:  
```shell
$ java -jar TS5AddonInstaller.jar [OPTIONS] <TEAMSPEAK_PATH>
```

| Option | Arguments | Description |
| ------ | --------- | ----------- |
| help, h | n/a | Displays a help text for command line usage |
| version, v | n/a| Displays the version of the installer |
| install | \<addon\> | Installs an addon from zip, folder or url, and applies patches to the TeamSpeak installation if required |
| uninstall | \<addon\> | Uninstalls an addon by id or name |
| list-installed | n/a | Displays installed addons |
| patch | n/a |  Patches the TeamSpeak installation to enable addon support |
| yes, y | n/a|  Automatically answers all prompts with 'yes' |

## Compatibility ##
| TeamSpeak | Windows | Linux | MacOS |
| ------- | ------- | ----- | ----- |
| Beta 75 | 2.3.0+ | 2.3.0+ | 2.3.0+ |
| Beta 74 | 2.2.0+ | 2.2.0+ | 2.2.0+ |
| Beta 73 | 2.1.0+ | 2.1.0+ | 2.1.0+ |
| Beta 72 | 2.0.0+ | 2.0.0+ | n/a |
| Beta 71 | ❌ | ❌ | n/a |
| Beta 70 | 1.0.0 - 2.0.0 | 1.0.0 - 2.0.0 | 1.0.0 - 2.0.0 |

## For Developers ##
If you want your addon to be compatible with the TS5 Addon Installer you need to create a new file called `addon.json` for your addon.
It marks the root of your addon source and needs to be distributed inside your addon artifact.
The `addon.json` contains the following attributes:

| Attribute | Description |
| --------- | ----------- |
| name | Specifies the name of the addon |
| id | Specifies the ID of the addon. Allowed characters: `A-Z`, `a-z`, `0-9` and `_` |
| version | Specifies the version of the addon, following [semver](https://semver.org/) semantics |
| inject | Specifies the file that contains the code that will be injected into the `index.html` |
| injection_point | Specifies the html tag the addon will be injected in. This can either be `HEAD` or `BODY`. Defaults to `HEAD` |
| inject_at | The position inside the html tag the addon will be injected in. This can eiter be `HEAD` or `TAIL`. Defaults to `TAIL` |
| sources | Specifies a folder relative to the `addon.json` that contains the actual source files of the addon. |
| installer | Optional: Specifies the required installer version for the addon, following [node-semver](https://github.com/npm/node-semver) semantics |
| teamspeak | Optional: Specifies the required teamspeak version for the addon, following [node-semver](https://github.com/npm/node-semver) semantics |

Example:
```json
{
	"id": "betterchat",
	"name": "BetterChat",
	"version": "1.0.0",
	"inject": "index.html",
	"injection_point": "HEAD",
	"inject_at": "TAIL",
	"sources": "src/",
	"installer": "~2.0.0",
	"teamspeak": "5.0.0-beta73"
}
```
A full example addon can be found [here](https://github.com/Exopandora/BetterChat)

## Patches ##
The installer features a builtin patcher to re-enable addon support for the latest versions of TeamSpeak 5.
Currently, the following patches are included with the installer:
- File validation bypass (does not include unknown file check bypass)
- Domain validation bypass

## Building from source ##

### Prerequisites ###
1. Java Development Kit 8 or later

### Windows exe ###
1. Run `gradlew createExe` in the root directory of this repository
2. The windows exe can be found in `./build/launch4j/`

### Universal jar ###
1. Run `gradlew build` in the root directory of this repository
2. The universal jar can be found in `./build/libs/`

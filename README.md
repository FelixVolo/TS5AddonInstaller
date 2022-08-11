# TS5 Addon Installer #
The TS5 Addon Installer is an unofficial tool that simplifies the process of installing and managing addons for TeamSpeak 5.

⚠️ Addons need to be reinstalled after every TeamSpeak update

## List of included Addons

| Addon | Description |
| ----- | ----------- |
| [BetterChat](https://github.com/Exopandora/BetterChat) | Support for BBCodes and rich embeds | 

## Installing Addons ##
The TS5 Addon Installer can automatically download and install addons or install them from zip files.

### Automatic Download ###
1. Select your TS5 installation directory
2. Select the addon you want to install
3. Click on "Install"

### From Zip File ###
1. Select your TS5 installation directory
2. Select "Zip File" as addon
3. Select the addon zip file you want to install
4. Click "Install"

## Uninstalling Addons ##
The TS5 Addon Installer can also automatically detect addons you have already installed with the installer and gives you the option to uninstall them again.

1. Select the "Uninstall" tab
2. Select your TS5 installation directory
3. Click on "Load"
4. Select the addon you want to uninstall
5. Click on "Uninstall"

## For Developers ##
If you want your addon to be compatible with the TS5 Addon Installer you need to create a new file called `addon.json` for your addon. It marks the root of your addon source and needs to be distributed inside your addon artifact. The `addon.json` contains the following attributes:

| Attribute | Description |
| --------- | ----------- |
| name      | The name of the addon |
| id        | The ID of the addon. Allowed characters: `A-Z`, `a-z`, `0-9` and `_` |
| version   | The version of the addon. Needs to follow maven artifact version conventions |
| inject    | The file that contains the code that will be injected into the `index.html` |
| injection_point    | The html tag the addon will be injected in. This can either be `HEAD` or `BODY`. Defaults to `HEAD` |
| inject_at    | The position inside the html tag the addon will be injected in. This can eiter be `HEAD` or `TAIL`. Defaults to `TAIL` |
| sources   | Specifies a folder inside the zip file relative to the `addon.json` that contains the actual source files of the addon which will be extracted to a new folder named after the addon id |

Example:
```
{
	"id": "betterchat",
	"name": "BetterChat",
	"version": "1.0.0",
	"inject": "index.html",
	"injection_point": "HEAD",
	"inject_at": "TAIL",
	"sources": "src/"
}
```
A full example addon can be found [here](https://github.com/Exopandora/BetterChat)

## Building from source ##

### Prerequisites ###
1. Java Development Kit 8 or later

### Windows exe ###
1. Run `gradlew createExe` in the root directory of this repository
2. The windows exe can be found in `./build/launch4j/`

### Universal jar ###
1. Run `gradlew build` in the root directory of this repository
2. The universal jar can be found in `./build/libs/`

# PintoManhunt

PintoManhunt is a Minecraft plugin for running customizable Manhunt games, inspired by the popular speedrunner vs hunter challenge. It supports multiple games at once, flexible roles, and integrates with Minecraft advancements and worlds.

## Features

- Create and manage multiple Manhunt games simultaneously
- Assign players as Speedrunners, Hunters, or Spectators
- Hunters receive compasses that track the nearest Speedrunner
- Automatic world management (Overworld, Nether, End)
- Game timer and win conditions (Ender Dragon kill or all Speedrunners dead)
- Advancement revocation for Speedrunners
- Easy-to-use commands and tab completion

## Installation

1. Place the `PintoManhunt.jar` file in your server's `plugins` folder.
2. Start or restart your Minecraft server.
3. (Optional) Use the [Multiverse-NetherPortals](https://dev.bukkit.org/projects/multiverse-netherportals) plugin to link Nether and End worlds for proper portal functionality.
Without it, any nether portals will send the player to world_the_nether and any end portals will send the player to world_the_end.

## Commands

All commands require the `pintomanhunt.use` permission.

```
/manhunt <create|remove|join|leave|status|start> [args]
```

### Command Reference

- `/manhunt create <type>`  
  Create a new Manhunt game. `<type>` is optional (default: `normal`).

- `/manhunt join <UUID> <speedrunner|hunter|spectator>`  
  Join a game by UUID as a specific role.

- `/manhunt leave <UUID>`  
  Leave a game by UUID.

- `/manhunt remove <UUID>`  
  Remove a game by UUID.

- `/manhunt status <UUID>`  
  View the status of a game.

- `/manhunt start <UUID>`  
  Start a game (requires at least one Hunter and one Speedrunner).

## How to Play

1. Create a game: `/manhunt create`
2. Have players join as Speedrunners or Hunters:  
   `/manhunt join <UUID> speedrunner`  
   `/manhunt join <UUID> hunter`
3. Start the game: `/manhunt start <UUID>`
4. Hunters use compasses to track Speedrunners.
5. The game ends when all Speedrunners die (Hunters win) or a Speedrunner kills the Ender Dragon (Speedrunners win).

## Permissions

- `pintomanhunt.use` — Allows use of all plugin commands.

## Notes

- The plugin automatically creates Nether and End worlds if they do not exist.
- For best results, use Multiverse-NetherPortals to link portals between worlds.

## License

MIT License

## Credits

Developed by PintoJ.

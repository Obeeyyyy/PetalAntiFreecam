# PetalAntiFreecam
Hide blocks below a configured Y level to prevent freecam.

## Features

- Configurable cutoff and restore Y levels
- Global chunk refresh budget to ensure the plugin never causes network congestion
- An efficient and optimized implementation

## Permissions

| Permission | Description |
|------------|-------------|
| `antifreecam.reload` | Reloads the plugin configuration |

## Commands

| Command | Description |
|---------|-------------|
| `/antifreecam reload` | Reload the configuration |

## Configuration
```yaml
restore-below-y: 30 # at what point we send the full chunk
hide-blocks-below-y: 16 # cutoff for blocks in the chunk

# Global budget so that we don't accidentally nuke netty
chunk-refresh-budget-per-tick: 16
```

# Playlist API Documentation

## Overview
The playlist feature allows channels to create multiple playlists, and each playlist can contain multiple videos. This implements a many-to-many relationship between playlists and videos.

## Key Features
- ✅ Channels can create multiple playlists
- ✅ Videos can belong to multiple playlists
- ✅ Public/Private playlist visibility
- ✅ Playlist management (create, update, delete)
- ✅ Add/Remove videos from playlists

## API Endpoints

### 1. Create Playlist
```http
POST /api/playlists/create
Content-Type: application/json

{
    "channelId": 1,
    "name": "My Favorite Videos",
    "description": "Collection of my favorite videos",
    "isPublic": true
}
```

### 2. Add Video to Playlist
```http
POST /api/playlists/{playlistId}/videos/{videoId}
```

### 3. Remove Video from Playlist
```http
DELETE /api/playlists/{playlistId}/videos/{videoId}
```

### 4. Get Channel Playlists (All)
```http
GET /api/playlists/channel/{channelId}
```

### 5. Get Channel Public Playlists
```http
GET /api/playlists/channel/{channelId}/public
```

### 6. Get Playlist by ID
```http
GET /api/playlists/{playlistId}
```

### 7. Update Playlist
```http
PUT /api/playlists/{playlistId}
Content-Type: application/json

{
    "name": "Updated Playlist Name",
    "description": "Updated description",
    "isPublic": false
}
```

### 8. Delete Playlist
```http
DELETE /api/playlists/{playlistId}
```

## Database Changes

### New Table: `playlist_videos`
This junction table manages the many-to-many relationship:
- `playlist_id` (Foreign Key to playlists)
- `video_id` (Foreign Key to videos)

### Updated `playlists` Table
- Added `is_public` column
- Added `created_at` and `updated_at` timestamps
- Added proper table name annotation

### Updated Relationships
- `PlayList` ↔ `Video`: Many-to-Many relationship
- `Channel` → `PlayList`: One-to-Many relationship (unchanged)

## Entity Changes

### PlayList Entity
- Added `isPublic`, `createdAt`, `updatedAt` fields
- Changed to `@ManyToMany` relationship with videos
- Added helper methods for getting video count and channel info

### Video Entity
- Changed from single `playlist` to `List<PlayList> playlists`
- Videos can now belong to multiple playlists

### Channel Entity
- Initialized `playLists` list to prevent null pointer exceptions

## Usage Examples

1. **Create a playlist for a channel:**
   ```bash
   curl -X POST http://localhost:8080/api/playlists/create \
   -H "Content-Type: application/json" \
   -d '{"channelId": 1, "name": "Tech Tutorials", "description": "Programming tutorials", "isPublic": true}'
   ```

2. **Add a video to playlist:**
   ```bash
   curl -X POST http://localhost:8080/api/playlists/1/videos/5
   ```

3. **Get all playlists for a channel:**
   ```bash
   curl http://localhost:8080/api/playlists/channel/1
   ```

## Notes
- Videos can be in multiple playlists simultaneously
- Deleting a playlist removes all video associations but doesn't delete the videos
- Private playlists are only visible through the "all playlists" endpoint
- The application automatically manages the bidirectional relationship between playlists and videos
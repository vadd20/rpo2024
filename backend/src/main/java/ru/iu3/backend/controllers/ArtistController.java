package ru.iu3.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.iu3.backend.models.Artist;
import ru.iu3.backend.repositories.ArtistRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ArtistController {
    @Autowired
    ArtistRepository artistRepository;

    @GetMapping("/artists")
    public List getAllArtists() {
        return artistRepository.findAll();
    }

    @PostMapping("/artists")
    public ResponseEntity<Object> createArtist(@RequestBody Artist artist) throws Exception{
        try {
            Artist newArtist = artistRepository.save(artist);
            return new ResponseEntity<Object>(newArtist, HttpStatus.OK);
        } catch (Exception ex) {
            String error;
            if (ex.getMessage().contains("artists.name_UNIQUE")) {
                error = "artistalreadyexists";
            } else {
                error = "undefinederror";
            }
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", error);
            return ResponseEntity.ok(errorMap);
        }
    }


    @PutMapping("/artists/{id}")
    public ResponseEntity<Artist> updateArtist(@PathVariable(value = "id") Long artistId,
                                               @RequestBody Artist artistDetails) {
        Artist artist = null;
        Optional<Artist> cc = artistRepository.findById(artistId);
        if (cc.isPresent()) {
            artist = cc.get();
            artist.name = artistDetails.name;
            artistRepository.save(artist);
            return ResponseEntity.ok(artist);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "artist not found");
        }
    }

    @DeleteMapping("/artists/{id}")
    public ResponseEntity<Object> deleteArtist(@PathVariable(value = "id") Long artistId) {
        Optional<Artist> artist = artistRepository.findById(artistId);
        Map<String, Boolean> response = new HashMap<>();
        if (artist.isPresent()) {
            artistRepository.delete(artist.get());
            response.put("deleted", Boolean.TRUE);
        } else {
            response.put("deleted", Boolean.FALSE);
        }
        return ResponseEntity.ok(response);
    }
}
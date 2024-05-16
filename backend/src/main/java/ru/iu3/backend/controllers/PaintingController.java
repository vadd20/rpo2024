package ru.iu3.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.iu3.backend.models.Painting;
import ru.iu3.backend.repositories.MuseumRepository;
import ru.iu3.backend.repositories.PaintingRepository;
import ru.iu3.backend.tools.DataValidationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/v1")
public class PaintingController {
    // По аналогии у нас будет два репозитория
    @Autowired
    PaintingRepository paintingRepository;

    @Autowired
    MuseumRepository museumRepository;

    /**
     * Метод, который возвращает список всех картин, которые есть в базе данных
     *
     * @return - список картин
     */
    @GetMapping("/paintings")
    public Page<Painting> getAllPaintings(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return paintingRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    @GetMapping("/paintings/{id}")
    public ResponseEntity<Painting> getPainting(@PathVariable(value = "id") Long paintingID)
            throws DataValidationException {
        Painting painting = paintingRepository.findById(paintingID)
                .orElseThrow(() -> new DataValidationException("Картина не была найдена"));

        return ResponseEntity.ok(painting);
    }

    /**
     * Метод, который добавляет картины в базу данных
     *
     * @param painting - картины
     * @return - заголовок. Ок/не ок
     */
    @PostMapping("/paintings")
    public ResponseEntity<Object> createPainting(@RequestBody Painting painting) throws DataValidationException {
        try {
            Painting newPainting = paintingRepository.save(painting);
            return new ResponseEntity<Object>(newPainting, HttpStatus.OK);
        } catch (Exception exception) {
            if (exception.getMessage().contains("paintings_name_key")) {
                throw new DataValidationException("Эта картина уже есть в базе");
            } else {
                throw new DataValidationException(exception.getMessage());
            }
        }
    }

    /**
     * Метод, обновляющий данные по картинам
     *
     * @param id              - ID картины
     * @param paintingDetails - сведения по картинам
     * @return - ОК/не ОК
     */
    @PutMapping("/paintings/{id}")
    public ResponseEntity<Painting> updatePainting(@PathVariable(value = "id") Long id,
                                                   @RequestBody Painting paintingDetails) throws DataValidationException {
        try {
            Painting painting = paintingRepository.findById(id)
                    .orElseThrow(() -> new DataValidationException("Картина не может быть обновлена"));

            // Сведения о картинах
            painting.name = paintingDetails.name;
            painting.museum = paintingDetails.museum;
            painting.artist = paintingDetails.artist;
            painting.year = paintingDetails.year;

            paintingRepository.save(painting);

            return ResponseEntity.ok(painting);
        } catch (Exception exception) {
            if (exception.getMessage().contains("ConstraintViolationException")) {
                throw new DataValidationException("Эта картина уже есть в базе");
            } else {
                throw new DataValidationException(exception.getMessage());
            }
        }
    }

    @PostMapping("/deletepaintings")
    public ResponseEntity deletePaintings(@Validated @RequestBody List<Painting> paintingList) {
        paintingRepository.deleteAll(paintingList);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Метод, который осуществляет удаление картины
     *
     * @param paintingID - ID картины
     * @return - статус: удален/не удален
     */
    @DeleteMapping("/paintings/{id}")
    public ResponseEntity<Object> deletePainting(@PathVariable(value = "id") Long paintingID) {
        Optional<Painting> cc = paintingRepository.findById(paintingID);
        Map<String, Boolean> resp = new HashMap<>();

        if (cc.isPresent()) {
            paintingRepository.delete(cc.get());
            resp.put("deleted", Boolean.TRUE);
        } else {
            resp.put("deleted", Boolean.FALSE);
        }

        return ResponseEntity.ok(resp);
    }
}
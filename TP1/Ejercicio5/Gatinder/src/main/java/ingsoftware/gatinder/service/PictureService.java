package ingsoftware.gatinder.service;

import java.util.Optional;
import java.util.UUID;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ingsoftware.gatinder.entity.Picture;
import ingsoftware.gatinder.repository.PictureRepository;

@Service
public class PictureService {
    @Autowired private PictureRepository pictureRepository;

    @Transactional public Picture create(MultipartFile file) throws ErrorService {
        try {
            if (file == null) {
                throw new ErrorService("El archivo de imagen es nulo");
            }

            Picture picture = new Picture();
            picture.setId(UUID.randomUUID().toString());
            picture.setMime(file.getContentType());
            picture.setData(file.getBytes());
            return pictureRepository.save(picture);
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al guardar la imagen");
        }
    }

    @Transactional public Picture update(String pictureId, MultipartFile file) throws ErrorService {
        try {
            if (file != null) {
                Optional<Picture> response = pictureRepository.findById(Long.valueOf(pictureId));
                if (response.isPresent()) {
                    Picture picture = response.get();
                    picture.setMime(file.getContentType());
                    picture.setData(file.getBytes());
                    return pictureRepository.save(picture);
                } else {
                    throw new ErrorService("No se encontró la imagen con el ID proporcionado");
                }
            } else {
                throw new ErrorService("El archivo de imagen es nulo");
            }
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al actualizar la imagen");
        }
    }
}

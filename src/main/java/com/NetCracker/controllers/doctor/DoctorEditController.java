package com.NetCracker.controllers.doctor;



import com.NetCracker.repositories.doctor.DoctorRepository;
import com.NetCracker.services.doctor.DoctorUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController

@RequestMapping({"/update"})
public class DoctorEditController {

    @Autowired
    private DoctorUserService doctorUserService;

    @Autowired
    private DoctorRepository doctorUserRepository;


//    @PostMapping
//    public void save(@RequestBody PostRequest request) {
//        doctorUserService.save(request);
//    }
//
//    @PutMapping("{id}/publish")
//    public void publishUnpublish(@PathVariable String id, @RequestBody PostRequest request) {
//        doctorUserService.changePublishedFlag(id, request);
//    }
//
//    @PutMapping("{id}")
//    public void update(@PathVariable String id, @RequestBody PostRequest request) {
//        Optional<Post> post = doctorUserService.findById(id);
//        if (post.isPresent()) {
//            doctorUserService.update(id, request);
//        } else {
//            doctorUserService.save(request);
//        }
//    }


}

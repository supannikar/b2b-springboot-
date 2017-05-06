package no.api.web.v1.controller;

import no.api.model.BasketModel;
import no.api.model.BasketProductModel;
import no.api.service.BasketProductService;
import no.api.service.BasketService;
import no.api.web.v1.mapper.BasketProductRequestMapper;
import no.api.web.v1.mapper.BasketProductResponseMapper;
import no.api.web.v1.mapper.BasketRequestMapper;
import no.api.web.v1.mapper.BasketResponseMapper;
import no.api.web.v1.transport.BasketProductTransport;
import no.api.web.v1.transport.BasketTransport;
import no.api.web.v1.transport.ResponseTransport;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiBodyObject;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Api(name = "Basket", description = "B2B Order Service API")
@RestController
@RequestMapping(value = "/api/b2b/v1/baskets", produces = MediaType.APPLICATION_JSON_VALUE)
public class BasketController {

    @Autowired
    private BasketService basketService;

    @Autowired
    private BasketProductService basketProductService;

    @ApiMethod(
            path = "/v1/baskets", verb = ApiVerb.POST,
            description = "Create new basket by member",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@ApiBodyObject @RequestBody @Valid BasketTransport basketTransport) {

//        BasketModel basketModel = new BasketModel();
//        basketModel.setBasketName(basketTransport.getBasketName());
//        basketModel.setBasketDesc(basketTransport.getBasketDesc());
//        basketModel.setMemberId(basketTransport.getMemberId());
//        if(basketTransport.getBasketProductTransports() != null){
//            basketModel.setBasketProductModel(new BasketProductRequestMapper().map(basketTransport.getBasketProductTransports()));
//        }

        BasketModel basketModel = new BasketRequestMapper().map(basketTransport);
        BasketModel saveBasket = basketService.save(basketModel);
        if(basketTransport.getBasketProductTransports().size() > 0){
            basketTransport.getBasketProductTransports().stream().forEach(each -> {
                BasketProductModel basketProductModel = new BasketProductModel();
                basketProductModel.setBasketId(saveBasket.getId());
                basketProductModel.setProductId(each.getProductId());
                basketProductModel.setProductQnty(each.getProductQuantity());
                basketProductService.save(basketProductModel);
            });
        }
        return new ResponseEntity<>(new BasketResponseMapper().map(saveBasket), HttpStatus.CREATED);
    }

    @ApiMethod(
            path = "/v1/baskets/{id}", verb = ApiVerb.PUT,
            description = "Edit Basket detail",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity update(@ApiBodyObject @RequestBody @Valid BasketTransport basketTransport,
                                 @PathVariable @ApiPathParam(name = "id") Long id) {

        BasketModel model = basketService.findById(id);
        if(model == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        BasketModel basketModel = new BasketRequestMapper().map(basketTransport);
        BasketModel updateBasket = basketService.save(basketModel);

        if(basketTransport.getBasketProductTransports().size() > 0){
            basketTransport.getBasketProductTransports().stream().forEach(each -> {
                if(each.getId() != null){
                    BasketProductModel basketProductModel = basketProductService.findById(each.getId());
                    if(basketProductModel != null){
                        if(each.getProductQuantity() == 0){
                            basketProductService.delete(each.getId().intValue());
                        }else {
                            basketProductService.save(new BasketProductRequestMapper().map(each));
                        }
                    }
                }else {
                    each.setBasketId(updateBasket.getId());
                    basketProductService.save(new BasketProductRequestMapper().map(each));
                }
            });
        }

        return new ResponseEntity<>(new BasketResponseMapper().map(updateBasket), HttpStatus.ACCEPTED);
    }

    @ApiMethod(
            path = "/v1/baskets/{id}", verb = ApiVerb.GET,
            description = "Retrieve basket by id",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity findById(@PathVariable @ApiPathParam(name = "id") Long id) {

        BasketModel model = basketService.findById(id);
        if(model == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<BasketProductModel> basketProductModels = basketProductService.listProductByBasketId(model.getId());
        if(basketProductModels.size() > 0){
            model.setBasketProductModel(basketProductModels);
        }
        return new ResponseEntity<>(new BasketResponseMapper().map(model), HttpStatus.OK);
    }

    @ApiMethod(
            path = "/v1/baskets", verb = ApiVerb.GET,
            description = "Retrieve all basket list",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity listAll() {
        List<BasketModel> listAll = basketService.listAll();
        List<BasketTransport> transports = new BasketResponseMapper().map(listAll);
        transports.stream().forEach(each -> {
            List<BasketProductModel> basketProductModels = basketProductService.listProductByBasketId(each.getId());
            if(basketProductModels.size() > 0){
                each.setBasketProductTransports(new BasketProductResponseMapper().map(basketProductModels));
            }
        });

        ResponseTransport<BasketTransport> responseTransport = new ResponseTransport<>(transports.size(), transports);

        return new ResponseEntity<>(responseTransport, HttpStatus.OK);
    }


    @ApiMethod(
            path = "/v1/baskets/{id}", verb = ApiVerb.DELETE,
            description = "Delete basket by id",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable @ApiPathParam(name = "id") Integer id) {
        basketService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

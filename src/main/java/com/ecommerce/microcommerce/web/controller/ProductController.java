package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Stream;

@Api(description = "API pour es opérations CRUD sur les produits.")
@RestController
public class ProductController {

    @Autowired
    private ProductDao productDao;

    @GetMapping(value = "/Produits")
    public MappingJacksonValue listeProduits() {
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(productDao.findAll());
        produitsFiltres.setFilters(new SimpleFilterProvider().addFilter("monFiltreDynamique", SimpleBeanPropertyFilter.serializeAllExcept("prixAchat")));
        return produitsFiltres;
    }

    @ApiOperation(value = "Récupère un produit grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping(value = "/Produits/{id}")
    public Product afficherUnProduit(@PathVariable int id) {
        Product produit = productDao.findById(id);
        if (produit == null)
            throw new ProduitIntrouvableException("Le produit avec l'id " + id + " est INTROUVABLE. Écran Bleu si je pouvais.");
        return produit;
    }

    @PostMapping(value = "/Produits")
    public ResponseEntity<Void> ajouterProduit(@Valid @RequestBody Product product) {
        Product productAdded = productDao.save(product);
        if (productAdded == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.created(ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri()).build();
    }

    @DeleteMapping(value = "/Produits/{id}")
    public void supprimerProduit(@PathVariable int id) {
        productDao.deleteById(id);
    }

    @PutMapping(value = "/Produits")
    public void updateProduit(@RequestBody Product product) {
        productDao.save(product);
    }

    @GetMapping(value = "/AdminProduits")
    public Stream<String> calculerMargeProduit() {
        return productDao.findAll().stream().map(product -> product.toString() + ": " + (product.getPrix() - product.getPrixAchat()));
    }

    @GetMapping
    public MappingJacksonValue trierProduitsParOrdreAlphabetique() {
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(productDao.findAllByOrderByNom());
        produitsFiltres.setFilters(new SimpleFilterProvider().addFilter("monFiltreDynamique", SimpleBeanPropertyFilter.serializeAllExcept()));
        return produitsFiltres;
    }

    //Pour les tests
    @GetMapping(value = "test/produits/{prix}")
    public List<Product> testeDeRequetes(@PathVariable int prix) {
        return productDao.chercherUnProduitCher(prix);
    }
}
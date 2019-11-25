package com.ecommerce.microcommerce.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "prixAchat")
@Entity
@JsonFilter("monFiltreDynamique")
public class Product {

    @Id
    @GeneratedValue
    private int id;
    @Length(min = 3, max = 20, message = "Nom trop long ou trop court. Et oui messages sont plus stylés que ceux de Spring")
    private String nom;
    @Min(value = 1)
    private int prix;
    //information que nous ne souhaitons pas exposer
    private int prixAchat;
}
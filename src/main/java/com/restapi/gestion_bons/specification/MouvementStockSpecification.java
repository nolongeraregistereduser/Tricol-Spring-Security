package com.restapi.gestion_bons.specification;

import com.restapi.gestion_bons.entitie.MouvementStock;
import com.restapi.gestion_bons.entitie.enums.TypeMouvement;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MouvementStockSpecification {

    public static Specification<MouvementStock> hasId(Long id){
        return ((root, query, criteriaBuilder) -> {
            return id == null ? null : criteriaBuilder.equal(root.get("id"), id);
        });
    }
    public static Specification<MouvementStock> hasTypeMouvement(TypeMouvement typeMouvement){
        return (root, query, criteriaBuilder) ->
                typeMouvement == null ? null : criteriaBuilder.equal(root.get("typeMouvement"), typeMouvement);
    }

    public static Specification<MouvementStock> hasDateMouvement(LocalDateTime dateMouvement){
        return (root, query, criteriaBuilder) ->
                dateMouvement == null ? null : criteriaBuilder.equal(root.get("dateMouvement"), dateMouvement);
    }

    public static Specification<MouvementStock> hasQantite(Integer quantite){
        return (root, query, criteriaBuilder) ->
                quantite == null ? null : criteriaBuilder.equal(root.get("quantite"), quantite);
    }

    public static Specification<MouvementStock> hasPrixUnitaireLot(Double prixUnitaireLot){
        return ((root, query, criteriaBuilder) -> {

            if(prixUnitaireLot == null) return null;
            return criteriaBuilder.equal(root.get("prixUnitaireLot"), prixUnitaireLot);
        });
    };

    public static Specification<MouvementStock> hasProduit(Long idProduit){
        return ((root, query, criteriaBuilder) -> {
            if(idProduit == null) return null;
            return criteriaBuilder.equal(root.get("produit").get("id"), idProduit);
        });
    }

    public static Specification<MouvementStock> hasLot(Long idLot){
        return ((root, query, criteriaBuilder) -> {
           if (idLot == null) return null;
           return criteriaBuilder.equal(root.get("lot").get("id"), idLot);
        });
    }

    public static Specification<MouvementStock> hasBonDeSortie(Long idBonDeSortie){
        return ((root, query, criteriaBuilder) -> {
            if (idBonDeSortie == null) return null;
            return criteriaBuilder.equal(root.get("bonDeSortie").get("id"), idBonDeSortie);
        });
    }

    // get Quantity Between

    public static Specification<MouvementStock> quantityBetween(Integer min, Integer max){
        return ((root, query, criteriaBuilder) -> {
            if(min == null || max == null) return null;
            return criteriaBuilder.between(root.get("quantite"), min, max);
        });
    }

    // get dateMouvement Between
    public static Specification<MouvementStock> dateMouvementBetween(LocalDateTime start, LocalDateTime end){
        return ((root, query, criteriaBuilder) -> {
            if(start == null && end == null) return null;
            if(start == null) return criteriaBuilder.lessThanOrEqualTo(root.get("dateMouvement"), end);
            if(end == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("dateMouvement"), start);
            return criteriaBuilder.between(root.get("dateMouvement"), start, end);
        });
    }

//    public static Specification<MouvementStock> dateMouvementBetween(LocalDateTime start, LocalDateTime end){
//        return ((root, query, criteriaBuilder) -> {
//           if(start == null && end == null) return null;
//           return criteriaBuilder.between(root.get("dateMouvement"), start, end);
//        });
//    }



    public static Specification<MouvementStock> hasProduitReference(String reference){
        return (root, query, criteriaBuilder) ->
                reference == null ? null : criteriaBuilder.equal(root.get("produit").get("reference"), reference);
    }




}

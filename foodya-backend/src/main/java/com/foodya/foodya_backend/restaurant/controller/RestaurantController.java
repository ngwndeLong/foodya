package com.foodya.foodya_backend.restaurant.controller;

import com.foodya.foodya_backend.restaurant.dto.RestaurantResponse;
import com.foodya.foodya_backend.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurant", description = "Restaurant management APIs")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Operation(
        summary = "Get all restaurants",
        description = "Retrieve all active restaurants"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved restaurants",
            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        List<RestaurantResponse> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @Operation(
        summary = "Get restaurant by ID",
        description = "Retrieve a specific restaurant by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Restaurant found",
            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Restaurant not found"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(
            @Parameter(description = "Restaurant ID", example = "1")
            @PathVariable Long id) {
        RestaurantResponse restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    @Operation(
        summary = "Search restaurants",
        description = "Search restaurants by name (case-insensitive)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search results returned"
        )
    })
    @GetMapping("/search")
    public ResponseEntity<List<RestaurantResponse>> searchRestaurants(
            @Parameter(description = "Search keyword", example = "Italian")
            @RequestParam String keyword) {
        List<RestaurantResponse> restaurants = restaurantService.searchRestaurants(keyword);
        return ResponseEntity.ok(restaurants);
    }

    @Operation(
        summary = "Get restaurants by cuisine",
        description = "Filter restaurants by cuisine type"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Restaurants filtered by cuisine"
        )
    })
    @GetMapping("/cuisine/{cuisine}")
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByCuisine(
            @Parameter(description = "Cuisine type", example = "Italian")
            @PathVariable String cuisine) {
        List<RestaurantResponse> restaurants = restaurantService.getRestaurantsByCuisine(cuisine);
        return ResponseEntity.ok(restaurants);
    }

    @Operation(
        summary = "Get popular restaurants",
        description = "Get restaurants sorted by popularity (total reviews and rating)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Popular restaurants returned"
        )
    })
    @GetMapping("/popular")
    public ResponseEntity<List<RestaurantResponse>> getPopularRestaurants() {
        List<RestaurantResponse> restaurants = restaurantService.getPopularRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @Operation(
        summary = "Get restaurants by minimum rating",
        description = "Filter restaurants with rating greater than or equal to specified value"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Restaurants filtered by rating"
        )
    })
    @GetMapping("/rating")
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByRating(
            @Parameter(description = "Minimum rating", example = "4.0")
            @RequestParam(defaultValue = "4.0") Double minRating) {
        List<RestaurantResponse> restaurants = restaurantService.getRestaurantsByMinRating(minRating);
        return ResponseEntity.ok(restaurants);
    }
}

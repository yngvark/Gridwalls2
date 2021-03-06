package com.yngvark.gridwalls.microservices.zombie;

@SuppressWarnings({ "WeakerAccess", "unused" })
public class ZombieTest {
//    private ZombieMovedSerializer zombieMovedSerializer;

//    @BeforeEach
//    public void before() {
//        zombieMovedSerializer = new ZombieMovedSerializer(new CoordinateSerializer());
//    }
//
//    @Test
//    public void should_always_change_coordinate_and_never_stand_still_on_next_turn() {
//        // Given
//        MapCoordinates mapCoordinates = new MapCoordinates(10, 10);
//        CoordinateFactory coordinateFactory = new CoordinateFactory(mapCoordinates);
//
//        Coordinate initCoord = mapCoordinates.center();
//        Zombie zombie = new Zombie(netCom, coordinateFactory, zombieMovedSerializer, initCoord);
//
//        Coordinate lastCoord = initCoord;
//        for (int i = 0; i < 1000; i++) {
//            // When
//            zombie.nextTurn();
//
//            // Then
//            ZombieMoved zombieMoved = zombieMovedSerializer.deserialize(netCom.consume());
//
//            assertNotEquals(lastCoord, zombieMoved.getTargetCoordinate());
//            assertFalse(netCom.hasMoreEvents());
//
//            // Finally
//            lastCoord = zombieMoved.getTargetCoordinate();
//        }
//    }
//
//    @Test
//    public void should_visit_all_coords_of_a_small_map() {
//        // Given
//        MapCoordinates mapCoordinates = new MapCoordinates(2, 2);
//        CoordinateFactory coordinateFactory = new CoordinateFactory(mapCoordinates);
//
//        Coordinate initCoord = mapCoordinates.center();
//        Zombie zombie = new Zombie(netCom, coordinateFactory, zombieMovedSerializer, initCoord);
//
//        Set<Coordinate> visitedCoords = new HashSet<>();
//        for (int i = 0; i < 1000; i++) {
//            // When
//            zombie.nextTurn();
//
//            // Record result
//            ZombieMoved zombieMoved = zombieMovedSerializer.deserialize(netCom.consume());
//            visitedCoords.add(zombieMoved.getTargetCoordinate());
//        }
//
//        // Then
//        assertEquals(mapCoordinates.allCoordinates(), visitedCoords);
//    }
}

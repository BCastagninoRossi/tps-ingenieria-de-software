-- import Stack
-- import Truck
-- import Palet
-- import Route

-- main :: IO ()
-- main = do
--     let route = newR ["CityA", "CityB", "CityC"]
--     let truck = newT 2 5 route
--     let palet1 = newP "CityA" 3
--     let palet2 = newP "CityB" 4
--     let palet3 = newP "CityC" 2

--     let truckLoaded1 = loadT truck palet1
--     let truckLoaded2 = loadT truckLoaded1 palet2
--     let truckLoaded3 = loadT truckLoaded2 palet3

--     putStrLn $ "Truck after loading: " ++ show truckLoaded3

--     let truckUnloaded = unloadT truckLoaded3 "CityB"
--     putStrLn $ "Truck after unloading at CityB: " ++ show truckUnloaded

import Control.Exception
import System.IO.Unsafe
import Stack
import Truck
import Palet
import Route
import Distribution.Parsec (parsecLeadingCommaList)

testF :: Show a => a -> Bool
testF action = unsafePerformIO $ do
    result <- tryJust isException (evaluate action)
    return $ case result of
        Left _ -> True
        Right _ -> False
  where
    isException :: SomeException -> Maybe ()
    isException _ = Just ()

-- Variables
route = newR ["CityA", "CityB", "CityC"]
truck = newT 2 5 route
palet1 = newP "CityA" 3
palet2 = newP "CityB" 4
palet3 = newP "CityC" 2
-- palet4 = newP "CityD" (-1)
stack = newS 5


-- Tests
testPalet1 = destinationP palet1 == "CityA" --ok
testPalet2 = netP palet1 == 3 --ok
testPalet3 = testF (newP "CityD" (-1)) --ok

testRoute1 = inOrderR route "CityA" "CityB" --ok
testRoute2 = inRouteR route "CityA" --ok 
testRoute3 = inOrderR route "CityB" "CityA" --ok 
testRoute4 = inRouteR route "CityH" --ok

testStack1 = freeCellsS stack == 5 --ok
testStack2 = netS stack == 0 --ok
testStack3 = holdsS stack palet1 route == True --ok


-- testStack4 = destinationP (palets !! 0) == "CityA" && netS (stackS stack palet1) == 3
--   where Sta palets _ = stackS stack palet1
testStack5 = freeCellsS (popS (stackS stack palet1) "CityA") == 5

testTruck1 = freeCellsT truck == 10
testTruck2 = netT truck == 0
testTruck3 = freeCellsT (loadT truck palet1) == 9 && netT (loadT truck palet1) == 3
testTruck4 = freeCellsT (unloadT (loadT truck palet1) "CityA") == 10

-- Función para mostrar esperado vs. real
printTest :: String -> Bool -> Bool -> IO ()
printTest name expected actual =
  putStrLn $ name ++ " | Expected: " ++ show expected ++ " | Actual: " ++ show actual

main :: IO ()
main = do
    printTest "testPalet1" True testPalet1
    printTest "testPalet2" True testPalet2
    printTest "testPalet3" True testPalet3

    printTest "testRoute1" True testRoute1
    printTest "testRoute2" True testRoute2
    printTest "testRoute3" False testRoute3
    printTest "testRoute4" False testRoute4
    
    printTest "testStack1" True testStack1
    printTest "testStack2" True testStack2
    printTest "testStack3" True testStack3
    -- printTest "testStack4" True testStack4
    printTest "testStack5" True testStack5
    
    printTest "testTruck1" True testTruck1
    printTest "testTruck2" True testTruck2
    printTest "testTruck3" True testTruck3
    printTest "testTruck4" True testTruck4

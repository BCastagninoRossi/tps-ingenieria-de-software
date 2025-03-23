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

-- Variables para pruebas simples (no-excepción)
route = newR ["CityA", "CityB", "CityC"]
truck = newT 2 5 route
palet1 = newP "CityA" 3
palet2 = newP "CityB" 4
palet3 = newP "CityC" 2
stack = newS 5
palets = [newP "CityA" 3, newP "CityB" 4, newP "CityC" 2]

-- Variables para demostrar comportamiento con error
-- Se testearán con testF
-- No se definen directamente para no romper la ejecución,
-- sino que se usan dentro de testF.

-- Comparación de resultados (no usa testF)
testPalet1 = destinationP palet1 == "CityA"
testPalet2 = netP palet1 == 3
testRoute1 = inOrderR route "CityA" "CityB"
testRoute2 = inRouteR route "CityA"
testStack1 = freeCellsS stack == 5
testStack2 = netS stack == 0
testStack3 = holdsS stack palet1 route == True
testTruck1 = freeCellsT truck == 10
testTruck2 = netT truck == 0
testTruck3 = freeCellsT (loadT truck palet1) == 9 && netT (loadT truck palet1) == 3

-- Ejemplo de popS y unloadT con resultados esperados
testStackPop = freeCellsS (popS (stackS stack palet1) "CityA") == 5
testTruck4 = freeCellsT (unloadT (loadT truck palet1) "CityA") == 10

-- Sección con testF para verificar lanzamiento de excepciones
testNewPEx1 = testF (newP "CityD" 0)        -- peso <= 0
testNewTEx1 = testF (newT 0 5 route)        -- numBays <= 0
testNewTEx2 = testF (newT 2 0 route)        -- capacity <= 0
testLoadTEx1 = testF (loadT truck (newP "CityX" 3)) -- ciudad no en ruta
testStackEx1 = testF (newS 0)               -- pila con capacidad <= 0
testHoldsEx1 = testF (loadT truck (newP "CityC" 11)) -- net total > 10 en una pila

-- Función para mostrar esperado vs. actual
printTest :: String -> Bool -> Bool -> IO ()
printTest name expected actual =
  putStrLn $ name ++ " | Expected: " ++ show expected ++ " | Actual: " ++ show actual

-- Listado de tests sin uso de testF (no excepciones)
nonExceptionTests :: [(String, Bool, Bool)]
nonExceptionTests =
  [ ("testPalet1", True, testPalet1)
  , ("testPalet2", True, testPalet2)
  , ("testRoute1", True, testRoute1)
  , ("testRoute2", True, testRoute2)
  , ("testStack1", True, testStack1)
  , ("testStack2", True, testStack2)
  , ("testStack3", True, testStack3)
  , ("testStackPop", True, testStackPop)
  , ("testTruck1", True, testTruck1)
  , ("testTruck2", True, testTruck2)
  , ("testTruck3", True, testTruck3)
  , ("testTruck4", True, testTruck4)
  ]

-- Listado de tests que usan testF (excepciones)
exceptionTests :: [(String, Bool, Bool)]
exceptionTests =
  [ ("testNewPEx1 (peso <=0)", True, testNewPEx1)
  , ("testNewTEx1 (numBays<=0)", True, testNewTEx1)
  , ("testNewTEx2 (capacity<=0)", True, testNewTEx2)
  , ("testLoadTEx1 (ciudad no en ruta)", True, testLoadTEx1)
  , ("testStackEx1 (capacity <=0)", True, testStackEx1)
  , ("testHoldsEx1 (net>10)", True, testHoldsEx1)
  ]

-- Función para correr todos los tests (no se usa 'main')
runAllTests :: IO ()
runAllTests = do
  putStrLn "===== Tests Sin Excepción ====="
  mapM_ (\(n, e, a) -> printTest n e a) nonExceptionTests
  
  putStrLn "\n===== Tests Con Excepción (usando testF) ====="
  mapM_ (\(n, e, a) -> printTest n e a) exceptionTests
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


route = newR ["Rosario", "La Boca", "Chubut"]
truck = newT 2 5 route
palet1 = newP "Rosario" 3
palet2 = newP "La Boca" 4
palet3 = newP "Chubut" 2
stack = newS 5
palets = [newP "Rosario" 3, newP "La Boca" 4, newP "Chubut" 2]

testPalet1 = destinationP palet1 == "Rosario"
testPalet2 = netP palet1 == 3
testRoute1 = inOrderR route "Rosario" "La Boca"
testRoute2 = inRouteR route "Rosario"
testStack1 = freeCellsS stack == 5
testStack2 = netS stack == 0
testStack3 = holdsS stack palet1 route == True
testTruck1 = freeCellsT truck == 10
testTruck2 = netT truck == 0
testTruck3 = freeCellsT (loadT truck palet1) == 9 && netT (loadT truck palet1) == 3
testHoldsTrue  = holdsS (stackS (newS 2) (newP "Chubut" 9)) (newP "Chubut" 1) route == True
testHoldsFalse = holdsS (stackS (newS 2) (newP "Chubut" 10)) (newP "Chubut" 1) route == False
testStackPop = freeCellsS (popS (stackS stack palet1) "Rosario") == 5
testTruck4 = freeCellsT (unloadT (loadT truck palet1) "Rosario") == 10

testNewPEx1 = testF (newP "Cordoba" 0)
testNewTEx1 = testF (newT 0 5 route)
testNewTEx2 = testF (newT 2 0 route)        
testLoadTEx1 = testF (loadT truck (newP "Posadas" 3)) 
testStackEx1 = testF (newS 0)
testNoFreeCellsEx = testF (loadT (loadT (newT 1 1 route) palet1) palet1) 


printTest :: String -> Bool -> Bool -> IO ()
printTest name expected actual =
  putStrLn $ name ++ " | Expected: " ++ show expected ++ " | Actual: " ++ show actual

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
  , ("testHoldsTrue", True, testHoldsTrue)
  , ("testHoldsFalse", True, testHoldsFalse)
  ]

exceptionTests :: [(String, Bool, Bool)]
exceptionTests =
  [ ("testNewPEx1 (peso <=0)", True, testNewPEx1)
  , ("testNewTEx1 (numBays<=0)", True, testNewTEx1)
  , ("testNewTEx2 (capacity<=0)", True, testNewTEx2)
  , ("testLoadTEx1 (ciudad no en ruta)", True, testLoadTEx1)
  , ("testStackEx1 (capacity <=0)", True, testStackEx1)
  , ("testNoFreeCellsEx (no hay celdas libres)", True, testNoFreeCellsEx)
  ]

runAllTests :: IO ()
runAllTests = do
  putStrLn "===== Tests Sin Excepción ====="
  mapM_ (\(n, e, a) -> printTest n e a) nonExceptionTests
  
  putStrLn "\n===== Tests Con Excepción ====="
  mapM_ (\(n, e, a) -> printTest n e a) exceptionTests

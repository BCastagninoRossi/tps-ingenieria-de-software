module Stack ( Stack, newS, freeCellsS, stackS, netS, holdsS, popS )
  where

import Palet
import Route


data Stack = Sta [ Palet ] Int deriving (Eq, Show)

newS :: Int -> Stack  -- construye una Pila con la capacidad indicada
newS capacity | capacity <= 0 = error "Stack capacity must be greater than 0"
newS capacity = Sta [] capacity
-- capacity: cantidad máxima de palets en la pila

freeCellsS :: Stack -> Int  -- responde las celdas disponibles en la pila
freeCellsS (Sta palets capacity) = capacity - length palets
-- stack: pila sobre la que se consulta cuántos palets se pueden aún apilar

stackS :: Stack -> Palet -> Stack  -- apila el palet indicado en la pila
stackS (Sta palets capacity) palet
    | length palets < capacity && (netS (Sta palets capacity) + netP palet) <= 10 = Sta (palet : palets) capacity
    | otherwise = error "Stack is full or exceeds weight limit"
-- stack: la pila sobre la que se apila
-- palet: palet que se desea apilar
-- Dentro de la función:
-- lenght palets < capacity: verifica que la pila no esté llena
-- netS (Sta palets capacity) + netP palet <= 10: verifica que el peso de la pila no exceda las 10 toneladas
-- En caso de que alguna de las dos condiciones no se cumpla, se lanza un error
-- En el caso positivo se apila el palet en la pila con Sta (palet : palets) capacity donde Sta es el constructor de Stack, 
-- (palet : palets) es la lista de palets con el nuevo palet apilado y capacity es la capacidad de la pila)


netS :: Stack -> Int  -- responde el peso neto de los palets en la pila
netS (Sta palets _) = sum $ map netP palets
-- stack: la pila cuyos palets se suman
-- Dentro de la función:
-- map netP palets: se aplica la función netP a cada palet en la pila
-- sum: se suman los pesos de los palets



holdsS :: Stack -> Palet -> Route -> Bool  -- indica si la pila puede aceptar el palet considerando las ciudades en la ruta
holdsS (Sta [] _) _ _ = True
holdsS (Sta (topPalet : palets) capacity) palet route =
    inOrderR route (destinationP topPalet) (destinationP palet) && (netS (Sta (topPalet : palets) capacity) + netP palet) <= 10
-- stack: la pila sobre la que se consulta si puede aceptar el palet
-- palet: palet que se desea apilar
-- route: ruta que deben seguir los palets
-- Dentro de la función:
-- inOrderR route (destinationP topPalet) (destinationP palet): verifica que la ciudad de destino del palet a apilar sea posterior a la
--  ciudad de destino del palet en la cima de la pila
-- netS (Sta (topPalet : palets) capacity) + netP palet <= 10: verifica que el peso de la pila no exceda las 10 toneladas
-- En caso de que alguna de las dos condiciones no se cumpla, se retorna False
-- En caso positivo, se retorna True

popS :: Stack -> String -> Stack  -- quita los palets con destino en la ciudad indicada
popS (Sta [] capacity) _ = Sta [] capacity
popS (Sta (p:ps) capacity) destino
    | destinationP p == destino = popS (Sta ps capacity) destino
    | otherwise = Sta (p:ps) capacity
-- stack: la pila de la que se quitan palets
-- destino: ciudad en la que se descargan palets
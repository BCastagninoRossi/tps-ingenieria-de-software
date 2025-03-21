module Palet ( Palet, newP, destinationP, netP )
  where

data Palet = Pal String Int deriving (Eq, Show)

newP :: String -> Int -> Palet   -- construye un Palet dada una ciudad de destino y un peso en toneladas
newP destino peso | peso <= 0 = error "Weight must be greater than 0"
                  | otherwise = Pal destino peso

-- ciudad: la ciudad de destino del palet
-- peso: el peso en toneladas del palet

destinationP :: Palet -> String  -- responde la ciudad destino del palet
destinationP (Pal destino _) = destino
-- palet: el palet cuya ciudad de destino se consulta

netP :: Palet -> Int             -- responde el peso en toneladas del palet
netP (Pal _ peso) = peso
-- palet: el palet cuyo peso se consulta

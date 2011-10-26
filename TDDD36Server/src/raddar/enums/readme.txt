File: readme.txt

[2011-10-26] Börje:
Detta paket innehåller (ska innehålla) samma enums/konstanter som klienten innehåller.
Dessa inkluderar enums för meddelandens prioritet (MessagePiority), meddelandens typ
(MessageType), osv. Använd dessa, och dess metoder, för att ange meddelandens prioriteter
och typer när man behöver skapa ett nytt meddelande. 

Exempel 1:
Message m = new TextMessage(MessageType.TEXT, "Bob", "Alice", MessagePriority.NORMAL); // Normal prioritet

Exempel 2:
TextMessage m = new TextMessage(MessageType.convert("text/plain"), "Bob", "Alice", MessagePriority.HIGH);	// Hög prioritet

Notis:
"text/plain" är HTTP standard för att definiera att det är text som skickas.
"image/jpeg" --||-- en jpeg bild som skickas.
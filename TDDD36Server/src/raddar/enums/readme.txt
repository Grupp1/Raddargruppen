File: readme.txt

[2011-10-26] B�rje:
Detta paket inneh�ller (ska inneh�lla) samma enums/konstanter som klienten inneh�ller.
Dessa inkluderar enums f�r meddelandens prioritet (MessagePiority), meddelandens typ
(MessageType), osv. Anv�nd dessa, och dess metoder, f�r att ange meddelandens prioriteter
och typer n�r man beh�ver skapa ett nytt meddelande. 

Exempel 1:
Message m = new TextMessage(MessageType.TEXT, "Bob", "Alice", MessagePriority.NORMAL); // Normal prioritet

Exempel 2:
TextMessage m = new TextMessage(MessageType.convert("text/plain"), "Bob", "Alice", MessagePriority.HIGH);	// H�g prioritet

Notis:
"text/plain" �r HTTP standard f�r att definiera att det �r text som skickas.
"image/jpeg" --||-- en jpeg bild som skickas.
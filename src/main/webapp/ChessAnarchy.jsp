<!doctype html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Chess Anarchy, inspired by Twitch Plays Pok&#233;mon</title>
    <script type="text/javascript" src="/chessanarchy/chessanarchy.nocache.js"></script>
    <script>
    	var user = ${userJson};
    </script>
    <style>
    	header {
    		margin-left: 10px;
    	}
    	.expand {
    		box-sizing: border-box;
			width: 100%;
			height: 100%;
			
    	}
    	#mainContent {
    		display:inline-block;
    		border: 2px solid gray;
    		padding:10px;
    		margin-bottom: 10px;
    		margin-right: 10px;
    		min-width: 848px;
    		min-height: 600px;
    	}
    	#essay{
    		display:inline-block;
    		width: 300px;
    		padding: 10px;
    		border: 2px solid gray;	
    		vertical-align:top;
    		height: 600px;
    		overflow:auto;
    	}
    </style>
</head>
<body>
	<header>
		<h1>Chess Anarchy, inspired by Twitch Plays Pok&#233;mon</h1>
		<h2>By Richard Wallis</h2>
	</header>
	<div id="mainContent">
	<noscript>
        <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; 
            background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif;">
            Your web browser must have JavaScript enabled
            in order for this application to display correctly.
        </div>
    </noscript>	
	</div>
	<div id="essay">
		A couple of weeks ago, a few thousand people began playing a game of Pok&#233;mon.  By sending a message to a chat window any of them could press a button on a virtual Game Boy. 
		The result was a barely organized chaos that managed to beat the game over 16 sleepless days.
		<p>I built Anarchy Chess to find out what happens when you play chess in the same way.</p>
		<p>You can make a move by dragging a piece across the board or by typing the move into the chat window.  Your move is a vote that influences the move that will be made by your team. 
		 The way your vote is counted is determined by your team's system of government, which is chosen at the beginning of the game.</p>
		<h3>The Systems of Government are:</h3>
		<h4>Anarchy</h4>
		<p>Under anarchy the first legal move received by the server is immediately played.</p>
		<h4>Democracy</h4>
		<p>Under democracy votes are counted after 30 seconds and the most popular move is played.</p>
		<h4>Hipsterism</h4>
		<p>Hipsters liked your move before it became popular.  Under Hipsterism the votes are counted after 30 seconds and then the least popular move is made.</p>
		<hr>
		<a href="https://github.com/rdwallis/ChessAnarchy">Chess Anarchy's source as well as instructions on how to create your own system of government are on Github.</a>
	
		
		 
	</div>
	
	<script>
		(function(doc, wnd) {
			var essay = doc.getElementById("essay");
			var mainContent = doc.getElementById("mainContent");
			
			function resize() {
				if (isEssayOnRight()) {
					essay.style.height = Math.max(600, mainContent.clientHeight - 20) + "px";
					essay.style.width = Math.min(wnd.innerWidth - 1150, 400) + "px";
				} else {
					essay.style.width = (mainContent.clientWidth  - 20)+ "px";
					essay.style.height = "300px";
				}
			}
			
			function isEssayOnRight() {
				return wnd.innerWidth > 1450;
			}
			
			var timeOut;
			function deferResize(time) {
				wnd.clearTimeout(timeOut);
				timeOut = wnd.setTimeout(resize, time);
			}
			
			wnd.onresize = function() {
				  deferResize(100);
			};
			resize();
			deferResize(1500);
			deferResize(5000);
			deferResize(15000);
			
		})(document, window);
	</script>

        
    
</body>
</html>

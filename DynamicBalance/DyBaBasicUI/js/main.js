
window.onload = function () {
    // TODO:: Do your initialization job

    // add eventListener for tizenhwkey
    document.addEventListener('tizenhwkey', function(e) {
        if(e.keyName == "back")
	try {
	    tizen.application.getCurrentApplication().exit();
	} catch (ignore) {
	}
    });

    document.addEventListener('deviceready', onDeviceReady, false);

    function onDeviceReady() {
        console.log('Cordova features now available');
        
        
    }
    
    
    
    
    var myCallbackInterval = 100; // 
    var sampleWindow = 30000; //
    var heart_rates = [];
    var avgBPM = 0;
    var counter = 0;
    var time = 0;
    var connected = false;
    var box = document.querySelector('#textbox');
    var circle = document.querySelector('#circle');
    var HRMrawsensor = tizen.sensorservice.getDefaultSensor("HRM_RAW");
    var listenerId_stationary;
    var listenerId_walking;
    var listenerId_running;
    var listenerId_vehicle;
    var activity_type = "nope";
    var last3HR = [];
    var last3activities = [];
    var ans = "n";
    var beats;
    
    var confirmCallback = function(buttonIndex) {
        console.log('Selected button was ' + buttonIndex);
        if(buttonIndex==1)
        	ans="y";
        else ans="n";
        sendMessages(ans);
    };
    
    function onchangedCB(hrmInfo) {
        //console.log('Heart Rate: ' + hrmInfo.heartRate);
        heart_rates.push(hrmInfo.heartRate);
        
        counter+= myCallbackInterval;
        //console.log('counter ; '+counter);
        if (counter >= sampleWindow) {
            /* Stop the sensor after detecting a few changes */
            //tizen.humanactivitymonitor.stop('HRM');
            //console.log('Array size: '+heart_rates.length);
            avgBPM = heart_rates.reduce(function(a, b) { return a + b; }, 0) / heart_rates.length;
            if(avgBPM<0)
            	avgBPM = 0;
            else if(avgBPM>180)
            	avgBPM = 180;
            console.log("BPM: "+ avgBPM);
            console.log("activity: "+ activity_type);
            
            if(last3HR.length == 3)
            	{
            	if(activity_type == last3activities[0] && !!last3activities.reduce(function(a, b){ return (a === b) ? a : NaN; }))
            		{
            		//AHR
            		HRC = avgBPM - last3HR.reduce(function(a, b) { return a + b; }, 0) / 3;
            		AHR = HRC/beats;
            		console.log("AHR: "+AHR);
            		sendMessages("AHR"+AHR.toString()+":"+beats.toString());
            		if(AHR>1)
            			{
            			navigator.vibrate(1000);
            			var ans;
            			var r=confirm("Stressed?");
            			if (r==true)
            				ans="y";
            			else
            				ans="n";
            			sendMessages(ans);
            			}
            		
            		}
            	last3HR.shift();
            	last3activities.shift();
            	}
            
            last3HR.push(avgBPM);
            last3activities.push(activity_type);
            
            console.log("last3HR: " + last3HR);
            console.log("last3activities: " + last3activities);
            circle.setAttribute('r',avgBPM);
            box.innerHTML = avgBPM;
            data_to_send = avgBPM.toString()+":"+activity_type;
            console.log("data_to_send: " + data_to_send);
            sendMessages(data_to_send);
            counter = 0;
            heart_rates = [];
        }
    }
    
    function errorCallback(error)
    {
      console.log(error.name + ": " + error.message);
    }
    
    function listener(info)
    {
      console.log("type: "      + info.type);
      console.log("timestamp: " + info.timestamp);
      console.log("accuracy: "  + info.accuracy);

      //tizen.humanactivitymonitor.removeActivityRecognitionListener(listenerId, errorCallback);
      activity_type = info.type;
    }
    
    function onerrorCB(error) {
        console.log('Error occurred. Name:' + error.name + ', message: ' + error.message);
    }
    
    function onsuccessCB() {
        console.log("HRMRaw sensor start");
    }
    
    function onchangedRAW(sensorData) {
    	//console.log("" + sensorData.lightIntensity);
    	
    }
    
    var option = {
    	    'callbackInterval': myCallbackInterval
    	};
    
    var textbox = document.querySelector('.contents');
    
    textbox.addEventListener("click", function(e){
    	//e.target.removeEventListener(e.type, arguments.callee);
    	if(box.innerHTML == "Start")
    		{
    		
    		
    		box.innerHTML = "Stop";
        	tizen.humanactivitymonitor.start('HRM', onchangedCB, onerrorCB, option);
        	HRMrawsensor.setChangeListener(onchangedRAW, 50, 0);
        	HRMrawsensor.start(onsuccessCB);
        	listenerId_walking = tizen.humanactivitymonitor.addActivityRecognitionListener("WALKING", listener, errorCallback);
        	listenerId_stationary = tizen.humanactivitymonitor.addActivityRecognitionListener("STATIONARY", listener, errorCallback);
        	listenerId_running = tizen.humanactivitymonitor.addActivityRecognitionListener("RUNNING", listener, errorCallback);
        	listenerId_vehicle = tizen.humanactivitymonitor.addActivityRecognitionListener("IN_VEHICLE", listener, errorCallback);
    		}
    	else
    		{
    		box.innerHTML = "Start";
    		tizen.humanactivitymonitor.stop('HRM');
    		HRMrawsensor.unsetChangeListener();
    		HRMrawsensor.stop();
    		}
    	
    	
        
    	
    });
    
    
 // /////////////////////SAP//////////////////////////
    

    var SAAgent = null;
    var SASocket = null;
    var CHANNELID = 110;
    var ProviderAppName = "DyBaCompanion";

    connect();


    function onReceive(channelId, data) {
	// for example
		console.log(" onreceive: "+data);
		//circle.setAttribute('r',data);
		beats = data;
    }
    
    function sendMessages(data) {
    	try {
    		SASocket.sendData(CHANNELID, data);
    	} catch(err) {
    		console.log("exception [" + err.name + "] msg[" + err.message + "]");
    	}
    }



    function connect() {
    	connected = true;
    	//alert('Trying to connect...');
    	if (SASocket) {
    		alert('Already connected!');
    		return false;
    	}

    	try {
    		webapis.sa.requestSAAgent(onsuccess, function(err) {
    			console.log(err);
    			alert(err);
    			//connected = false;
    		});
    	} catch (err) {
    		console.log("exception [" + err.name + "] msg[" + err.message + "] yes here");
    		alert("exception [" + err.name + "] msg[" + err.message + "]");
    		//connected = false;
    	}
    }
    
    
    function onsuccess(agents) {
    	try {
    		if (agents.length > 0) {
    			SAAgent = agents[0];

    			SAAgent.setPeerAgentFindListener(peerAgentFindCallback);
    			SAAgent.findPeerAgents();
    			//alert(" onsuccess " + SAAgent.name);
    		
    		} else {
    			alert("Not found SAAgent!!");
    			console.log(" onsuccess else");
    			//connected = false;
    		}
    	} catch (err) {
    		console.log("exception [" + err.name + "] msg[" + err.message + "]");
    		alert("exception [" + err.name + "] msg[" + err.message + "]");
    		//connected = false;
    	}
    }
    
    var peerAgentFindCallback = {
    		onpeeragentfound : function(peerAgent)

    		{
    			//alert("appmame: " + peerAgent.appName);
    			try {
    				if (peerAgent.appName === ProviderAppName) {
    					console.log("PerAgentFindCallback:onpeeragentfound " + peerAgent.appname + "||" + ProviderAppName);
    					SAAgent.setServiceConnectionListener(agentCallback);
    					SAAgent.requestServiceConnection(peerAgent);
    					//alert('succes2');
    					connected = true;
    				} else {
    					console.log("peerAgentFindCallback::onpeeragentfound else");
    					alert("Not expected app!! : " + peerAgent.appName);
    				}
    			} catch (err) {
    				console.log("peerAgentFindCallback::onpeeragentfound exception [" + err.name + "] msg[" + err.message + "]");
    				alert("peerAgentFindCallback::onpeeragentfound exception [" + err.name + "] msg[" + err.message + "]");
    				//connected = false;
    			}
    		},
    		onerror : function(err) {
    			alert(err);
    			//connected = false;
    		}
    	};
    
    var agentCallback = {

    		onconnect : function(socket) {
    			console.log("agentCallback onconnect" + socket);
    			SASocket = socket;
    			alert("Connected to phone!");
    			try {
    	    		SASocket.setDataReceiveListener(onReceive);
    	        } catch(err) {
    	    		console.log("exception [" + err.name + "] msg[" + err.message + "]");
    	        }
    			SASocket.setSocketStatusListener(function(reason) {
    				console.log("Service connection lost, Reason : [" + reason + "]");
    				disconnect();
    				
    			});
    		},
    		onerror : function(err) {
    			alert(err);
    			//connected = false;
    		}
    	};
    
    function disconnect() {
    	try {
    		if (SASocket !== null) {
    			console.log("Disconnect SASocket not null");
    			SASocket.close();
    			SASocket = null;
    			alert("Connection closed");
    			connected = false;
    		}
    	} catch (err) {
    		console.log("exception [" + err.name + "] msg[" + err.message + "]");
    	}
    }
    

    
    

    
};

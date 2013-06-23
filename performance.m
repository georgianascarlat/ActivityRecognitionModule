function performance()
	M = csvread('activity/activity_recognition.txt');
	m = size(M,1);
	prediction = M(:,2);
	ground_truth = M(:,4);
	
	comp = sum(prediction == ground_truth);
	accuracy = 100*comp/m;
	
	disp('Accuracy: ');
	disp(accuracy);
	
end

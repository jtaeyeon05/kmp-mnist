from collections import OrderedDict

import torch.nn as nn

import config


"""
Model Name Map (Except Dropout)

# layer1
layer1.conv1_1.weight
layer1.conv1_1.bias
layer1.conv1_2.weight
layer1.conv1_2.bias

# layer2
layer2.conv2_1.weight
layer2.conv2_1.bias
layer2.conv2_2.weight
layer2.conv2_2.bias

# layer3
layer3.fc3_1.weight
layer3.fc3_1.bias
layer3.fc3_2.weight
layer3.fc3_2.bias
"""

class MnistCNN(nn.Module):
    def __init__(self):
        super(MnistCNN, self).__init__()
        self.layer1 = nn.Sequential(
            OrderedDict([
                ("conv1_1", nn.Conv2d(1, 16, kernel_size=3, stride=1, padding=1)),
                ("relu1_1", nn.ReLU()),
                ("conv1_2", nn.Conv2d(16, 16, kernel_size=3, stride=1, padding=1)),
                ("relu1_2", nn.ReLU()),
                ("pool1", nn.MaxPool2d(kernel_size=2, stride=2)),
                ("drop1", nn.Dropout(config.DROP_OUT_RATE)),
            ])
        )
        self.layer2 = nn.Sequential(
            OrderedDict([
                ("conv2_1", nn.Conv2d(16, 32, kernel_size=3, stride=1, padding=1)),
                ("relu2_1", nn.ReLU()),
                ("conv2_2", nn.Conv2d(32, 32, kernel_size=3, stride=1, padding=1)),
                ("relu2_2", nn.ReLU()),
                ("pool2", nn.MaxPool2d(kernel_size=2, stride=2)),
                ("drop2", nn.Dropout(config.DROP_OUT_RATE)),
            ])
        )
        self.layer3 = nn.Sequential(
            OrderedDict([
                ("flatten3", nn.Flatten()),
                ("fc3_1", nn.Linear(32 * 7 * 7, 256, bias=True)),
                ("relu3_1", nn.ReLU()),
                ("drop1", nn.Dropout(config.DROP_OUT_RATE)),
                ("fc3_2", nn.Linear(256, config.NUM_CLASSES, bias=True)),
            ])
        )

    def forward(self, x):
        x = self.layer1(x)
        x = self.layer2(x)
        x = self.layer3(x)
        return x

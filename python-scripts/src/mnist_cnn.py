import torch.nn as nn
import torch.nn.functional as F

import config


class MnistCNN(nn.Module):
    def __init__(self):
        super(MnistCNN, self).__init__()
        self.layer1 = nn.Sequential(
            nn.Conv2d(1, 32, kernel_size=3, stride=1, padding=1),
            nn.ReLU(),
            nn.Conv2d(32, 32, kernel_size=3, stride=1, padding=1),
            nn.ReLU(),
            nn.MaxPool2d(kernel_size=2, stride=2),
            nn.Dropout(config.DROP_OUT_RATE),
        )
        self.layer2 = nn.Sequential(
            nn.Conv2d(32, 64, kernel_size=3, stride=1, padding=1),
            nn.ReLU(),
            nn.Conv2d(64, 64, kernel_size=3, stride=1, padding=1),
            nn.ReLU(),
            nn.MaxPool2d(kernel_size=2, stride=2),
            nn.Dropout(config.DROP_OUT_RATE),
        )
        self.layer3 = nn.Sequential(
            nn.Flatten(),
            nn.Linear(64 * 7 * 7, 512, bias=True),
            nn.ReLU(),
            nn.Dropout(config.DROP_OUT_RATE),
            nn.Linear(512, config.NUM_CLASSES, bias=True),
        )

    def forward(self, x):
        x = self.layer1(x)
        x = self.layer2(x)
        x = self.layer3(x)
        return F.log_softmax(x, dim=1)

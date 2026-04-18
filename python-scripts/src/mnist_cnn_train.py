import os
import random
import sys
import time
from datetime import datetime
from typing import Optional, Tuple

import numpy as np
import matplotlib.pyplot as plt
import torch
import torch.nn.functional as F
from loguru import logger
from torch import nn, optim
from torchvision import datasets, transforms

import config
import mnist_cnn


logger.remove()
logger.add(sys.stderr, format="<green>{time:YYYY-MM-DD HH:mm:ss.SSS}</green> | <level>{level:<8}</level> | <level>{message}</level>", level="TRACE")


# ========================================
# 학습 환경 초기화
# ========================================
logger.info("Initializing Training Environment...")

# 디바이스 선택
# noinspection PyArgumentList
if torch.cuda.is_available():
    device = "cuda"
    dtype = torch.float16
elif torch.backends.mps.is_available():
    device = "mps"
    dtype = torch.float16
else:
    device = "cpu"
    dtype = torch.float32
logger.debug(f"Selected the Device (Device: {device}, DType: {dtype})")

# 랜덤 스테이트 적용
random.seed(config.RANDOM_STATE)
np.random.seed(config.RANDOM_STATE)
torch.manual_seed(config.RANDOM_STATE)
if device == "cuda":
    torch.cuda.manual_seed(config.RANDOM_STATE)
    torch.cuda.manual_seed_all(config.RANDOM_STATE)
    torch.backends.cudnn.deterministic = True
    torch.backends.cudnn.benchmark = False
elif device == "mps":
    torch.mps.manual_seed(config.RANDOM_STATE)

# MNIST 데이터셋
mnist_train_transform = transforms.Compose([
    transforms.RandomRotation(10),
    transforms.ToTensor(),
    transforms.Normalize(mean=(0.1307,), std=(0.3081,))
])
mnist_test_transform = transforms.Compose([
    transforms.ToTensor(),
    transforms.Normalize(mean=(0.1307,), std=(0.3081,))
])

should_download = (
        not os.path.exists(config.MNIST_DATASET_PATH) or
        not os.listdir(config.MNIST_DATASET_PATH) or
        config.MUST_DOWNLOAD_DATASET
)
if should_download:
    logger.debug(f"Starting Download...")
mnist_train_dataset = datasets.MNIST(
    root=config.MNIST_DATASET_PATH,
    train=True,
    transform=mnist_train_transform,
    download=should_download,
)
mnist_test_dataset = datasets.MNIST(
    root=config.MNIST_DATASET_PATH,
    train=False,
    transform=mnist_test_transform,
    download=should_download,
)
if should_download:
    logger.debug(f"Finished Download MNIST Dataset")

mnist_train_dataloader = torch.utils.data.DataLoader(
    dataset=mnist_train_dataset,
    batch_size=config.BATCH_SIZE,
    shuffle=True,
    drop_last=True,
)
mnist_test_dataloader = torch.utils.data.DataLoader(
    dataset=mnist_test_dataset,
    batch_size=config.BATCH_SIZE,
    shuffle=False,
    drop_last=False,
)
if config.SHOW_MNIST_SAMPLE:
    sample_index = time.time_ns() // 1000 % len(mnist_train_dataset)
    sample_image, sample_label = mnist_train_dataset[sample_index]
    plt.title("MNIST SAMPLE")
    plt.imshow(
        X=sample_image.squeeze().numpy(),
        cmap='gray',
    )
    plt.text(
        s=f"Sample Index: {sample_index}/{len(mnist_train_dataset)}\n" +
          f"Sample Label: {sample_label}\n" +
          f"Image Type: {type(sample_image)}\n" +
          f"Image Size: {sample_image.shape}\n" +
          f"Label Type: {type(sample_label)}",
        x=0,
        y=0,
        va="top",
        ha="left",
        fontdict={
            "size": 10,
            "alpha": 0.5,
            "color": "white",
        },
    )
    plt.show()
logger.debug(f"Loaded MNIST Dataset (Train: {len(mnist_train_dataset)}, Test: {len(mnist_test_dataset)}, Path: {os.path.abspath(config.MNIST_DATASET_PATH)})")


# ========================================
# 학습
# ========================================
logger.info("Starting Training...")

# 모델, 옵티마이저, 스케줄려 로드
model = mnist_cnn.MnistCNN().to(device)
criterion = nn.CrossEntropyLoss()
optimizer = optim.Adam(model.parameters(), lr=config.LEARNING_RATE)
scheduler = optim.lr_scheduler.StepLR(optimizer, step_size=config.SCHEDULER_STEP_SIZE, gamma=config.SCHEDULER_GAMMA)
logger.debug("Loaded Model, Optimizer, and Scheduler")

# 학습, 테스트
def train(now_epoch: int):
    model.train()
    for batch_idx, (data, target) in enumerate(mnist_train_dataloader):
        data, target = data.to(device), target.to(device)
        optimizer.zero_grad()
        output = model(data)
        loss = criterion(output, target)
        loss.backward()
        optimizer.step()
        if batch_idx % 200 == 0:
            logger.trace(f"[Epoch {now_epoch}] Loss: {loss.item():.6f} ({batch_idx * len(data):>5}/{len(mnist_train_dataset):>5})")

def test(now_epoch: Optional[int] = None) -> Tuple[float, int, int]:
    model.eval()
    test_loss = 0
    correct = 0
    total = len(mnist_test_dataset)

    with torch.no_grad():
        for data, target in mnist_test_dataloader:
            data, target = data.to(device), target.to(device)
            output = model(data)
            test_loss += F.cross_entropy(output, target, reduction='sum').item()
            pred = output.argmax(dim=1, keepdim=True)
            correct += pred.eq(target.view_as(pred)).sum().item()
    test_loss /= total

    if now_epoch is not None:
        logger.debug(f"[Epoch {now_epoch}] Average loss: {test_loss:.6f}, Accuracy: {correct:>5}/{total:>5} ({100. * correct / total:.2f}%)")
    else:
        logger.debug(f"Average loss: {test_loss:.6f}, Accuracy: {correct:>5}/{total:>5} ({100. * correct / total:.2f}%)")

    return test_loss, correct, total

# 학습 실행
epoches = []
losses = []
accuracies = []

for epoch in range(1, config.NUM_EPOCHS + 1):
    train(epoch)
    test_result = test(epoch)
    epoches.append(epoch)
    losses.append(test_result[0])
    accuracies.append(100. * test_result[1] / test_result[2])
    scheduler.step()

if config.SHOW_TRAINING_RESULT:
    fig, axis1 = plt.subplots()
    axis2 = axis1.twinx()

    color_loss = "tab:red"
    axis1.set_ylabel("Loss", color=color_loss)
    lns1 = axis1.plot(epoches, losses, color=color_loss, marker="o", label="Loss")
    axis1.tick_params(axis="y", labelcolor=color_loss)

    color_acc = 'tab:blue'
    axis2.set_ylabel('Accuracy (%)', color=color_acc, fontsize=12)
    lns2 = axis2.plot(epoches, accuracies, color=color_acc, marker='s', label='Accuracy')
    axis2.tick_params(axis='y', labelcolor=color_acc)

    plt.title("TRAINING RESULT")
    plt.xlabel("EPOCH")
    lns = lns1 + lns2
    plt.legend(lns, [l.get_label() for l in lns], loc='center right', frameon=True, shadow=True)
    fig.tight_layout()
    plt.grid(True, axis="both", linestyle="--", alpha=0.5)
    plt.show()
logger.debug("Finished Training")

# 모델 저장
time_stamp = datetime.now().strftime("%Y%m%d_%H%M%S")
model_filename = f"MnistCNN_{time_stamp}.pth"
torch.save(model.state_dict(), f"{config.MODEL_PATH}/{model_filename}")
logger.debug(f"Saved Model (Path: {os.path.abspath(f"{config.MODEL_PATH}/{model_filename}")})")
